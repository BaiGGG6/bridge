package com.bai.bridge.base;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        map.put("bridge.plugin.dir", "ddd");
        map.put("bridge.config.file", "aaa");
        try {
            System.out.println(BridgeCoreConstants.CONFIG_FILE);
            System.out.println(BridgeCoreConstants.PLUGIN_FILE);
            injectConfig(map);
            System.out.println(BridgeCoreConstants.CONFIG_FILE);
            System.out.println(BridgeCoreConstants.PLUGIN_FILE);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void injectConfig(Map<String, String> map) throws NoSuchFieldException {
        try {
            Class<BridgeCoreConstants> constantsClass = BridgeCoreConstants.class;
            if(map.containsKey("bridge.plugin.dir")){
                injectFinalVal(constantsClass, "PLUGIN_FILE", map.get("bridge.plugin.dir"));
            }
            if(map.containsKey("bridge.config.file")){
                injectFinalVal(constantsClass, "CONFIG_FILE", map.get("bridge.config.file"));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void injectFinalVal(Class<?> cls, String filedName, String val) throws NoSuchFieldException, IllegalAccessException {
        Field target = cls.getDeclaredField(filedName);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        target.setAccessible(true);
        // 取消final
        int modifiers = modifiersField.getInt(target);
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(target, modifiers);
        // 重写设值
        target.set(null, val);
        // 设置final
        modifiers |= Modifier.FINAL;
        modifiersField.setInt(target, modifiers);
        target.setAccessible(false);
    }


}
