package com.bai.bridge.loader;

import com.bai.bridge.model.enums.SpaceMode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.jar.JarFile;

public enum ClassLoaderFactory {
    INSTANCE;

    public ClassLoader getBridgeJarClassLoader(File file, JarFile jarFile, SpaceMode spaceMode) throws MalformedURLException {
        return new BridgeJarClassLoader(file, ClassLoaderFactory.class.getClassLoader(), spaceMode != SpaceMode.ISOLATION, jarFile);
    }

    public ClassLoader getBridgeJarClassLoader(File file, SpaceMode spaceMode) throws IOException {
        return new BridgeJarClassLoader(file, ClassLoaderFactory.class.getClassLoader(), spaceMode != SpaceMode.ISOLATION, new JarFile(file.getPath()));
    }

}
