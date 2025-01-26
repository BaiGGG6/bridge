package com.bai.app.controller;

import cn.hutool.json.JSONUtil;
import com.bai.bridge.PluginProcessor;
import com.bai.bridge.base.DataCacheCenter;
import com.bai.bridge.model.PluginMeta;
import com.bai.app.PluginA;
import com.bai.app.PluginB;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BridgeController {

    @GetMapping("/pluginA")
    public void testPluginA() {
        PluginA slotImpl = DataCacheCenter.getSlotImpl(PluginA.class);
        if (slotImpl != null) {
            System.out.println(slotImpl.execute());
        }
    }

    @GetMapping("/pluginB")
    public void testPluginB() {
        PluginB slotImpl = DataCacheCenter.getSlotImpl(PluginB.class);
        if (slotImpl != null) {
            System.out.println(slotImpl.execute());
        }
    }

    @GetMapping("/pluginRun")
    public String getPluginRunTimeInfos() {
        String result = JSONUtil.toJsonStr(DataCacheCenter.getAllInfo());
        System.out.println(result);
        return result;
    }

    @GetMapping("/pluginRelease")
    public String releasePlugin(String sign, String version){
        PluginMeta pluginMeta = DataCacheCenter.getPluginMeta(sign, version);
        if(pluginMeta == null){
            return null;
        }
        PluginProcessor.releasePlugin(pluginMeta);
        return "success";
    }




}
