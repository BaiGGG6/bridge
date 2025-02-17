package com.bai.bridge;

import com.bai.bridge.analysis.service.PluginAnalyseService;
import com.bai.bridge.base.ConfigHandler;
import com.bai.bridge.base.SpiFactory;

import java.util.List;

public abstract class PluginStarter implements PluginStartService{

    public static void init(){
        // 优先加载配置文件
        loadPluginConfig();
        List<PluginStartService> pluginStartServices = SpiFactory.get(PluginStartService.class);
        for (PluginStartService pluginStartService : pluginStartServices) {
            pluginStartService.execute();
        }
    }

    public static void loadPluginConfig(){
        // 获取对应的配置信息，若有多个配置信息
        List<PluginAnalyseService> pluginAnalysisServices = SpiFactory.get(PluginAnalyseService.class);
        if(pluginAnalysisServices != null && !pluginAnalysisServices.isEmpty()){
            PluginAnalyseService adapter = pluginAnalysisServices.get(0);
            // 加载配置文件
            try {
                ConfigHandler.injectConfig(adapter.loadBridgeConfig());
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
