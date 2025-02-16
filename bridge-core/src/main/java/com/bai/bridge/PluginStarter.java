package com.bai.bridge;

import com.bai.bridge.analysis.service.PluginAnalyse;
import com.bai.bridge.base.ConfigHandler;
import com.bai.bridge.base.SpiFactory;

import java.util.List;

public class PluginStarter implements PluginStartService{

    public static void init(){
        List<PluginStartService> pluginStartServices = SpiFactory.get(PluginStartService.class);
        for (PluginStartService pluginStartService : pluginStartServices) {
            pluginStartService.execute();
        }
    }

    @Override
    public void execute() {
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
    }

}
