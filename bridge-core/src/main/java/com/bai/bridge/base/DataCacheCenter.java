package com.bai.bridge.base;

import com.bai.bridge.model.PluginMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DataCacheCenter {
    INSTANCE;

    // sign和version的映射
    private Map<String, List<String>> signAndVersionMap = new HashMap<>();

    // sign:version 插件缓存
    private Map<String, PluginMeta> pluginMetaMap = new HashMap<>();

    // sign:version 中所有的插槽
    private Map<String, List<String>> signVersionSlotMap = new HashMap<>();

    // 所有插槽和实现
    private Map<String, List<Class<?>>> slotImplMap = new HashMap<>();

    /**
     * 构建key sign|version
     * @param sign
     * @param version
     * @return
     */
    private String buildKey(String sign, String version){
        return sign + BridgeCoreConstants.SEPARATE + version;
    }

    public boolean pluginContains(PluginMeta pluginMeta){
        return pluginMetaMap.containsKey(buildKey(pluginMeta.getSign(), pluginMeta.getVersion()));
    }

    public void landingPluginInfo(String sign, String version, PluginMeta pluginMeta, List<Class<?>> classList){
        List<String> list = signAndVersionMap.getOrDefault(sign, new ArrayList<>());
        list.add(version);
        signAndVersionMap.put(sign, list);
        pluginMeta.setClassList(classList);
        pluginMetaMap.put(buildKey(sign, version), pluginMeta);
    }

    public void landingSignVersionSlotMap(String sign, String version, String slotName){
        String key = buildKey(sign, version);
        List<String> list = signVersionSlotMap.getOrDefault(key, new ArrayList<>());
        if(list.contains(slotName)){
            return;
        }
        list.add(slotName);
        signVersionSlotMap.put(key, list);
    }

    public void landingSlotImpl(String slotName, Class<?> cls){
        List<Class<?>> list = slotImplMap.getOrDefault(slotName, new ArrayList<>());
        list.add(cls);
        slotImplMap.put(slotName, list);
    }

    public void releasePluginInfo(String sign, String version){
        List<String> list = signAndVersionMap.getOrDefault(sign, new ArrayList<>());
        list.remove(version);
        if(list.isEmpty()){
            signAndVersionMap.remove(sign);
        }
        pluginMetaMap.remove(buildKey(sign, version));
    }

    public void releaseSignVersionClassMap(String sign, String version, String slotName){
        String key = buildKey(sign, version);
        List<String> list = signVersionSlotMap.getOrDefault(key, new ArrayList<>());
        if (!list.contains(slotName)){
            return;
        }
        list.remove(slotName);
        if(list.isEmpty()){
            signVersionSlotMap.remove(key);
        }
    }

    public void releaseSlotImpl(String slotName, Class<?> cls){
        List<Class<?>> classList = slotImplMap.getOrDefault(slotName, new ArrayList<>());
        if(!classList.contains(cls)){
            return;
        }
        classList.remove(cls);
        if(classList.isEmpty()){
            slotImplMap.remove(slotName);
        }
    }

    public Map<String, PluginMeta> getSignPluginMetaMap(){
        return pluginMetaMap;
    }

    public List<Map> getAllInfo(){
        List<Map> list = new ArrayList<>();
        list.add(signAndVersionMap);
        list.add(pluginMetaMap);
        list.add(signVersionSlotMap);
        list.add(slotImplMap);
        return list;
    }

    public <T> T getSlotImpl(Class<T> pClass){
        Class<?> aClass = slotImplMap.get(pClass.getName()).get(0);
        T pluginImpl = null;
        try {
            pluginImpl = (T) aClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return pluginImpl;
    }

    public PluginMeta getPluginMeta(String sign, String version) {
        return pluginMetaMap.get(buildKey(sign, version));
    }
}
