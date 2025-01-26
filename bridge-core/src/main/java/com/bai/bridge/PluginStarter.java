package com.bai.bridge;

import com.bai.bridge.analysis.service.PluginAnalyse;
import com.bai.bridge.base.ConfigHandler;
import com.bai.bridge.base.SpiFactory;

import java.util.List;

public class PluginStarter {


    public static void start(){
        // 初始化spi工厂
        SpiFactory.init();
        // 获取对应的配置信息，若有多个配置信息
        List<PluginAnalyse> pluginAnalyses = SpiFactory.get(PluginAnalyse.class);
        if(pluginAnalyses != null && !pluginAnalyses.isEmpty()){
            PluginAnalyse adapter = pluginAnalyses.get(0);
            // 加载配置文件
            try {
                ConfigHandler.injectConfig(adapter.loadBridgeConfig());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        // 加载plugin
        PluginProcessor.startLoadPlugin();
    }

}
