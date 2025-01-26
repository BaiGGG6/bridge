package com.bai.bridge.analysis.service;

import java.util.*;

public abstract class PluginAnalyseAbstract implements PluginAnalyse {


    @Override
    public Map<String, String> loadBridgeConfig() {
        return Collections.emptyMap();
    }


}
