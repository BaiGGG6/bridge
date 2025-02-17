package com.bai.bridge.base;

import com.bai.bridge.PluginStartService;
import com.bai.bridge.analysis.service.PluginAnalyseService;

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

    static {
        loadClass(PluginAnalyseService.class);
        loadClass(PluginStartService.class);
    }

    private static <T> void loadClass(Class<T> cls){
        ServiceLoader<T> load = ServiceLoader.load(cls);
        load.iterator().forEachRemaining(item -> {
            List<Object> objectList = spiCache.getOrDefault(cls, new ArrayList<>());
            objectList.add(item);
            spiCache.put(cls, objectList);
        });
    }

    public static <T> List<T> get(Class<T> key){
        return spiCache.get(key).stream().map(item -> (T) item).collect(Collectors.toList());
    }

}
