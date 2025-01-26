package com.plugin.info;

import com.bai.app.PluginA;
import com.bai.bridge.annotation.PluginExSlotInfo;

@PluginExSlotInfo(slot = "com.bai.app.PluginA")
public class pluginAImpl implements PluginA {
    @Override
    public Object execute() {
        return "asdsad";
    }
}
