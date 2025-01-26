package com.bai.bridge.loader;

import com.bai.bridge.Exception.PluginException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BridgeJarClassLoader extends URLClassLoader {

    private String basePath;
    private boolean isParentMode;
    private JarFile jarFile;

    public BridgeJarClassLoader(File file, ClassLoader parent, Boolean isParentMode, JarFile jarFile) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);
        this.isParentMode = isParentMode;
        this.basePath = file.getPath();
        this.jarFile = jarFile;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (isParentMode) {
            return super.loadClass(name);
        }
        Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }
        try {
            return findClass(name);
        } catch (Exception e) {
            return getParent().loadClass(name);
        }
    }
//
//    @Override
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        String concat = basePath.concat(File.separator).concat(name.replace('.', File.separatorChar).concat(".class"));
//
//        Path clsPath = Paths.get(concat);
//
//        if (!Files.exists(clsPath)) {
//            throw new ClassNotFoundException(name);
//        }
//
//        try {
//            byte[] classData = Files.readAllBytes(clsPath);
//            return defineClass(name, classData, 0, classData.length);
//        } catch (Exception e) {
//            throw new ClassNotFoundException(name, e);
//        }
//    }

    // 重写 findClass 方法，用于查找并加载指定名称的类
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 将类名转换为 JAR 包内的路径
        String entryName = name.replace('.', '/').concat(".class");
        JarEntry entry = jarFile.getJarEntry(entryName);
        if(entry == null){
            throw new ClassNotFoundException(name);
        }
        try {
            InputStream inputStream = jarFile.getInputStream(entry);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            // 从输入流读取数据并写入输出流
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] classBytes = outputStream.toByteArray();
            // 使用 defineClass 方法将字节数据转换为 Class 对象
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new PluginException("加载jar包类失败", e);
        }
    }

    public static void main(String[] args) {
        Path path = Paths.get("D:/project/spring-brick/bridgePlugin/bridge-spring-app/target/classes/plugins/boot-plugin-1.0-SNAPSHOT.jar/com/bai/app/controller/BridgeController.class");
        System.out.println(path);
    }

}
