package com.bai.bridge;

import com.bai.bridge.Exception.PluginException;
import com.bai.bridge.analysis.service.PluginAnalyseService;
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

    private static List<PluginAnalyseService> analysies;

    /**
     * 当前运行文件夹
     */
    private static String classPath = System.getProperty("user.dir");

    /**
     * 存储插件文件夹
     */
    private static String foldPath = classPath + File.separator + BridgeCoreConstants.PLUGIN_FILE;

    static {
        // 初始化adapter
        analysies = SpiFactory.get(PluginAnalyseService.class);
    }

    public static void bootstrapFoldPlugin(){
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
                loadJarPlugin(fileInfo);
            }
        }
    }

    /**
     * 加载jar插件
     * @param file
     */
    public static PluginMeta loadJarPlugin(File file){
        if(!file.getName().endsWith(BridgeCoreConstants.JAR_ENDS)){
            throw new PluginException("传入的文件不是jar包格式");
        }
        long start = System.currentTimeMillis();
        log.info("开始加载插件：{}", file.getName());
        // 构建配置文件
        PluginMeta pluginMeta = null;
        try {
            JarFile jarFile = new JarFile(file);
            pluginMeta = findToBuildPluginMeta(file);
            // 校验是否已经存在当前插件，若存在则打回
            if(DataCacheCenter.INSTANCE.pluginContains(pluginMeta)){
                throw new PluginException("当前插件已经被加载");
            }
            // 构建classLoader
            ClassLoader currentClassLoader = ClassLoaderFactory.INSTANCE.getBridgeJarClassLoader(file, jarFile, pluginMeta.getSpaceMode());
            // 校验当前的加载模式
            if(pluginMeta.getLoadMode().equals(LoadMode.MANUAL)){
                // 手动加载
                manualLoadJar(jarFile, currentClassLoader, pluginMeta);
            }else {
                // 自动加载
                autoLoadJar(jarFile, currentClassLoader, pluginMeta);
            }
            log.info("成功加载插件：{} | {} | {}, 加载模式: {}, 空间模式：{}, 耗时：{} ms", file.getName(), pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta.getLoadMode().name(), pluginMeta.getSpaceMode().name(), System.currentTimeMillis() - start);
        } catch (Exception | Error e) {
            log.error("当前插件加载失败: {}", file.getName(), e);
            releasePlugin(pluginMeta);
        }
        return pluginMeta;
    }

    /**
     * 卸载插件
     * @param pluginMeta
     */
    public static void releasePlugin(PluginMeta pluginMeta){
        if(pluginMeta == null){
            log.error("当前传入的Plugin为空，跳过释放");
            return;
        }
        log.info("开始释放插件：{} - {} , {}", pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta.getJarName());
        long start = System.currentTimeMillis();
        DataCacheCenter.INSTANCE.releasePluginInfo(pluginMeta.getSign(), pluginMeta.getVersion());
        analysies.forEach(analyse -> analyse.release(pluginMeta));
        log.info("释放插件完成：{} - {} , {}, 耗时: {}ms", pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta.getJarName(), System.currentTimeMillis() - start);
    }

    /**
     * 指定加载
     * @param jarFile
     * @param classLoader
     * @param pluginMeta
     * @throws ClassNotFoundException
     */
    private static void manualLoadJar(JarFile jarFile, ClassLoader classLoader, PluginMeta pluginMeta) throws ClassNotFoundException {
        try {
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
            DataCacheCenter.INSTANCE.landingPluginInfo(pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta, classList);
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

    /**
     * 自动加载
     * @param jarFile
     * @param classLoader
     * @param pluginMeta
     * @throws ClassNotFoundException
     * @throws MalformedURLException
     */
    private static void autoLoadJar(JarFile jarFile, ClassLoader classLoader, PluginMeta pluginMeta) throws ClassNotFoundException, MalformedURLException {
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
            DataCacheCenter.INSTANCE.landingPluginInfo(pluginMeta.getSign(), pluginMeta.getVersion(), pluginMeta, classList);
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

    public static PluginMeta findToBuildPluginMeta(File file) throws URISyntaxException, IOException {
        try (URLClassLoader configLoader = new URLClassLoader(new URL[]{file.toURI().toURL()})){

            // 构建properties
            Properties properties = new Properties();

            // 读取配置流
            try (InputStream inputStream = configLoader.getResourceAsStream(BridgeCoreConstants.CONFIG_FILE)){
                if(inputStream != null){
                    properties.load(inputStream);
                }
            }

            // 关闭loader
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
        }catch (Exception e){
            throw new PluginException("关闭出错", e);
        }

    }



}
