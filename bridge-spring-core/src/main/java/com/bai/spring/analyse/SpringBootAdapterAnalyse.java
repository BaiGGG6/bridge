package com.bai.spring.analyse;

import com.bai.bridge.analysis.service.PluginAnalyseAbstract;
import com.bai.bridge.model.PluginMeta;
import com.bai.spring.PluginAdapter;
import com.bai.spring.model.enums.BootClassEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SpringBootAdapterAnalyse extends PluginAnalyseAbstract {

//    @Value("bridge.plugin.dir")
//    private String bridgePluginDir;
//
//    @Value("bridge.config.file")
//    private String bridgeConfigFile;


//    @Override
//    public void analyse(PluginMeta pluginMeta, Class<?> cls) {
//        // 获取class注解映射
//        BootClassEnum clsType = BootClassEnum.getClsType(cls);
//        if(clsType == null){
//            return;
//        }
//        PluginAdapter.INSTANCE.processBean(clsType, cls);
//    }
//
//    @Override
//    public void release(PluginMeta pluginMeta, Class<?> cls) {
//        // 获取class注解映射
//        BootClassEnum clsType = BootClassEnum.getClsType(cls);
//        if(clsType == null){
//            return;
//        }
//        PluginAdapter.INSTANCE.releaseBean(clsType, cls);
//    }

    @Override
    public void analyse(PluginMeta pluginMeta) {
        Map<BootClassEnum, List<Class<?>>> clsMap = new HashMap<>();
        pluginMeta.getClassList().forEach(cls -> {
            BootClassEnum clsType = BootClassEnum.getClsType(cls);
            List<Class<?>> clsTypeList = clsMap.getOrDefault(clsType, new ArrayList<>());
            clsTypeList.add(cls);
            clsMap.put(clsType, clsTypeList);
        });
        PluginAdapter.INSTANCE.process(pluginMeta, clsMap);
    }

    @Override
    public void release(PluginMeta pluginMeta) {
        Map<BootClassEnum, List<Class<?>>> clsMap = new HashMap<>();
        pluginMeta.getClassList().forEach(cls -> {
            BootClassEnum clsType = BootClassEnum.getClsType(cls);
            List<Class<?>> clsTypeList = clsMap.getOrDefault(clsType, new ArrayList<>());
            clsTypeList.add(cls);
            clsMap.put(clsType, clsTypeList);
        });
        PluginAdapter.INSTANCE.release(pluginMeta, clsMap);
    }

    @Override
    public Map<String, String> loadBridgeConfig() {
        HashMap<String, String> map = new HashMap<>();
//        map.put("bridge.plugin.dir", bridgePluginDir);
//        map.put("bridge.config.file", bridgeConfigFile);
        return map;
    }


}
