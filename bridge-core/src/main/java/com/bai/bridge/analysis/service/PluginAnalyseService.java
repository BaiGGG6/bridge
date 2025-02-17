package com.bai.bridge.analysis.service;

import com.bai.bridge.model.PluginMeta;

import java.util.Map;

public interface PluginAnalyseService {

    void analyse(PluginMeta pluginMeta);

    Map<String, String> loadBridgeConfig();

    void release(PluginMeta pluginMeta);

}

