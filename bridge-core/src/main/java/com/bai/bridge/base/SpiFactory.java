package com.bai.bridge.base;

import com.bai.bridge.analysis.service.PluginAnalyse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * 加载对应的适配
 */
public class SpiFactory {

    private static final HashMap<Class<?>, List<Object>> spiCache = new HashMap<>();

    public static void init(){
        ServiceLoader<PluginAnalyse> load = ServiceLoader.load(PluginAnalyse.class);
        load.iterator().forEachRemaining(item -> {
            List<Object> objectList = spiCache.getOrDefault(PluginAnalyse.class, new ArrayList<>());
            objectList.add(item);
            spiCache.put(PluginAnalyse.class, objectList);
        });
    }

    public static <T> List<T> get(Class<T> key){
        return spiCache.get(key).stream().map(item -> (T) item).collect(Collectors.toList());
    }

}
