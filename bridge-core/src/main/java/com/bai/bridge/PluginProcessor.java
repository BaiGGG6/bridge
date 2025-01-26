package com.bai.bridge;

import cn.hutool.json.JSONUtil;
import com.bai.bridge.Exception.PluginException;
import com.bai.bridge.analysis.service.PluginAnalyse;
import com.bai.bridge.base.BridgeCoreConstants;
import com.bai.bridge.base.DataCacheCenter;
import com.bai.bridge.base.SpiFactory;
import com.bai.bridge.loader.ClassLoaderFactory;
import com.bai.bridge.model.PluginMeta;
import com.bai.bridge.model.enums.LoadMode;
import com.bai.bridge.model.enums.SpaceMode;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class PluginProcessor {

    private static List<PluginAnalyse> analysies;

    public static void startLoadPlugin(){
        // 初始化adapter
        analysies = SpiFactory.get(PluginAnalyse.class);
        // 加载file
        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(BridgeCoreConstants.PLUGIN_FILE);
        if(resource == null){
            log.info("未读取到{}文件夹，无插件加载", BridgeCoreConstants.PLUGIN_FILE);
            return;
        }
        File file = new File(resource.getFile());
        File[] files = file.listFiles();
        if (files != null) {
            for (File fileInfo : files) {
                if(fileInfo.getName().endsWith(BridgeCoreConstants.JAR_ENDS)){
                    long start = System.currentTimeMillis();
                    log.info("开始加载插件：{}", fileInfo.getName());
                    // 构建配置文件
                    PluginMeta pluginMeta = null;
                    try {
                        JarFile jarFile = new JarFile(fileInfo);
                        pluginMeta = findToBuildPluginMeta(fileInfo);
                        // 构建classLoader
                        ClassLoader currentClassLoader = ClassLoaderFactory.INSTANCE.getBridgeJarClassLoader(fileInfo, jarFile, pluginMeta.getSpaceMode());
                        // 校验当前的加载模式
                        if(pluginMeta.getLoadMode().equals(LoadMode.MANUAL)){
                            // 手动加载
                            manualLoadJar(jarFile, currentClassLoader, pluginMeta);
                        }else {
                            // 自动加载
                            autoLoadJarAll(jarFile, currentClassLoader, pluginMeta);
                        }
                    } catch (Exception | Error e) {
                        log.error("当前插件加载失败: {}", fileInfo.getName(), e);
                        releasePlugin(pluginMeta);
                    }
                    log.info("成功加载插件：{} | {} | {}, 加载模式: {}, 空间模式：{}, 耗时：{} ms", fileInfo.getName(), pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta.getLoadMode().name(), pluginMeta.getSpaceMode().name(), System.currentTimeMillis() - start);
                }
            }
        }
    }

    private static void manualLoadJar(JarFile jarFile, ClassLoader classLoader, PluginMeta pluginMeta) throws ClassNotFoundException {
        List<String> processedLoadClassPaths = pluginMeta.getProcessedLoadClassPaths();
        Enumeration<JarEntry> entries = jarFile.entries();
        List<Class<?>> classList = new ArrayList<>();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            // 只加载指定类
            if (entry.getName().endsWith(".class") && processedLoadClassPaths.stream().anyMatch(item -> entry.getName().startsWith(item))) {
                // 提取类名，将路径中的 / 转换为. 并去掉.class后缀
                String className = entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6);
                Class<?> targetClass = classLoader.loadClass(className);
                // 存储起来，当出现问题则直接报错
                classList.add(targetClass);
            }
        }
        // 插件加载进入数据中心
        DataCacheCenter.landingPluginInfo(pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta, classList);
        // 解析器进行加载
        analysies.forEach(item -> item.analyse(pluginMeta));
    }


    private static void autoLoadJarAll(JarFile jarFile, ClassLoader classLoader, PluginMeta pluginMeta) throws ClassNotFoundException, MalformedURLException {
        try {
            Enumeration<JarEntry> entries = jarFile.entries();
            List<Class<?>> classList = new ArrayList<>();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    // 提取类名，将路径中的 / 转换为. 并去掉.class后缀
                    String className = entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6);
                    Class<?> targetClass = classLoader.loadClass(className);
                    // 存储起来，当出现问题则直接报错
                    classList.add(targetClass);
                }
            }
            // 插件加载进入数据中心
            DataCacheCenter.landingPluginInfo(pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta, classList);
            // 解析器进行加载
            analysies.forEach(item -> item.analyse(pluginMeta));
        } finally {
            try {
                if(jarFile != null){
                    jarFile.close();
                }
            } catch (IOException e) {
                throw new PluginException(e);
            }
        }
    }

    public static void releasePlugin(PluginMeta pluginMeta){
        if(pluginMeta == null){
            log.error("当前传入的Plugin为空，跳过释放");
            return;
        }
        log.info("开始释放插件：{} - {} , {}", pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta.getJarName());
        long start = System.currentTimeMillis();
        DataCacheCenter.releasePluginInfo(pluginMeta.getSign(), pluginMeta.getVersion());
        analysies.forEach(analyse -> analyse.release(pluginMeta));
        log.info("释放插件完成：{} - {} , {}, 耗时: {}ms", pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta.getJarName(), System.currentTimeMillis() - start);
    }

    public static PluginMeta findToBuildPluginMeta(File file) throws URISyntaxException, IOException {
        URLClassLoader configLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
        URL configResource = configLoader.getResource(BridgeCoreConstants.CONFIG_FILE);

        // 构建properties
        Properties properties = new Properties();
        if(configResource == null){
            log.warn("当前插件 {} 未找到配置文件，走默认配置", file.getName());
        }else {
            properties.load(configResource.openStream());
        }
        properties.get(BridgeCoreConstants.CONFIG_INFO_SIGN);

        // 读取必填配置(未读取的都走默认配置)
        String pluginSign = properties.getProperty(BridgeCoreConstants.CONFIG_INFO_SIGN, file.getName());
        String pluginVersion = properties.getProperty(BridgeCoreConstants.CONFIG_INFO_VERSION, "default");
        String pluginLoadMode = properties.getProperty(BridgeCoreConstants.CONFIG_INFO_LOAD_MODE, "auto");
        String pluginSpaceMode = properties.getProperty(BridgeCoreConstants.CONFIG_INFO_SPACE_MODE, "isolation");
        String loadClassPaths = properties.getProperty(BridgeCoreConstants.CONFIG_INFO_LOAD_CLASS_PATH, "");

        // 清空properties中的必填配置
        properties.remove(BridgeCoreConstants.CONFIG_INFO_SIGN);
        properties.remove(BridgeCoreConstants.CONFIG_INFO_VERSION);
        properties.remove(BridgeCoreConstants.CONFIG_INFO_LOAD_MODE);
        properties.remove(BridgeCoreConstants.CONFIG_INFO_SPACE_MODE);
        properties.remove(BridgeCoreConstants.CONFIG_INFO_LOAD_CLASS_PATH);

        return new PluginMeta(file.getName(), pluginSign, pluginVersion, LoadMode.getBySign(pluginLoadMode), SpaceMode.getBySign(pluginSpaceMode), Arrays.asList(loadClassPaths.split(",")), properties);
    }



}
