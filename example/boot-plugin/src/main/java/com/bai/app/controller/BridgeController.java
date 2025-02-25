package com.bai.app.controller;

import cn.hutool.json.JSONUtil;
import com.bai.app.PluginA;
import com.bai.app.PluginB;
import com.bai.bridge.PluginProcessor;
import com.bai.bridge.base.DataCacheCenter;
import com.bai.bridge.model.PluginMeta;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BridgeController {

    @GetMapping("/pluginA")
    public void testPluginA() {
        PluginA slotImpl = DataCacheCenter.INSTANCE.getSlotImpl(PluginA.class);
        if (slotImpl != null) {
            System.out.println("当前是plugin");
            System.out.println(slotImpl.execute());
        }
    }

}
