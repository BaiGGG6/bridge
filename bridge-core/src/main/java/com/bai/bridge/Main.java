package com.bai.bridge;


import com.bai.bridge.base.DataCacheCenter;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {


    public static void main(String[] args) throws MalformedURLException {


        List<String> paths = new ArrayList<>();
        paths.add("123");
        paths.add("123");
        paths.add("123");
        String[] array = paths.toArray(new String[0]);

        System.out.println(Arrays.toString(array));

        System.out.println("Hello, World!");

        PluginStarter.start();

//        PluginA pluginAImpl = DataCacheCenter.getSlotImpl(PluginA.class);
//        PluginB pluginBImpl = DataCacheCenter.getSlotImpl(PluginB.class);
//
//        System.out.println(pluginAImpl.execute());
//        System.out.println(pluginBImpl.execute());
    }





}