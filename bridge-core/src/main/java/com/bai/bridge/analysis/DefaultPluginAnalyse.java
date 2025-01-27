package com.bai.bridge.analysis;

import com.bai.bridge.annotation.PluginExSlotInfo;
import com.bai.bridge.analysis.service.PluginAnalyseAbstract;
import com.bai.bridge.base.DataCacheCenter;
import com.bai.bridge.model.PluginMeta;

public class DefaultPluginAnalyse extends PluginAnalyseAbstract {

    @Override
    public void analyse(PluginMeta pluginMeta) {
        pluginMeta.getClassList().forEach(cls -> {
            PluginExSlotInfo annotation = cls.getAnnotation(PluginExSlotInfo.class);
            if(annotation != null){
                // 存储插槽和插槽实现映射
                DataCacheCenter.INSTANCE.landingSlotImpl(annotation.slot(), cls);
                // 存储插件和插槽映射
                DataCacheCenter.INSTANCE.landingSignVersionSlotMap(pluginMeta.getSign(), pluginMeta.getVersion(), annotation.slot());
            }
        });
    }

    @Override
    public void release(PluginMeta pluginMeta) {
        pluginMeta.getClassList().forEach(cls -> {
            PluginExSlotInfo annotation = cls.getAnnotation(PluginExSlotInfo.class);
            if(annotation != null){
                // 存储插槽和插槽实现映射
                DataCacheCenter.INSTANCE.releaseSlotImpl(annotation.slot(), cls);
                // 存储插件和插槽映射
                DataCacheCenter.INSTANCE.releaseSignVersionClassMap(pluginMeta.getSign(), pluginMeta.getVersion(), annotation.slot());
            }
        });
    }
}
