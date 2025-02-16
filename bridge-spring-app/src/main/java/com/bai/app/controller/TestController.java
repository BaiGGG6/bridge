package com.bai.app.controller;

import cn.hutool.json.JSONUtil;
import com.bai.app.PluginA;
import com.bai.app.PluginB;
import com.bai.bridge.PluginProcessor;
import com.bai.bridge.base.DataCacheCenter;
import com.bai.bridge.model.PluginMeta;
import com.bai.spring.injector.UrlCacheCenter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class TestController {

    @GetMapping("/unit")
    public String getTime(){
        return "tets";
    }

    @GetMapping("/pluginA")
    public void testPluginA() {
        PluginA slotImpl = DataCacheCenter.INSTANCE.getSlotImpl(PluginA.class);
        if (slotImpl != null) {
            System.out.println("当前是app");
            System.out.println(slotImpl.execute());
        }
    }

    @GetMapping("/pluginB")
    public void testPluginB() {
        PluginB slotImpl = DataCacheCenter.INSTANCE.getSlotImpl(PluginB.class);
        if (slotImpl != null) {
            System.out.println(slotImpl.execute());
        }
    }

    @GetMapping("/pluginRun")
    public String getPluginRunTimeInfos() {
        String result = JSONUtil.toJsonStr(DataCacheCenter.INSTANCE.getAllInfo());
        System.out.println(result);
        return result;
    }

    @GetMapping("/pluginRelease")
    public String releasePlugin(String sign, String version){
        PluginMeta pluginMeta = DataCacheCenter.INSTANCE.getPluginMeta(sign, version);
        if(pluginMeta == null){
            return null;
        }
        PluginProcessor.releasePlugin(pluginMeta);
        return "success";
    }

    @GetMapping("/urlRun")
    public String getUrlCenterData(){
        System.out.println(UrlCacheCenter.INSTANCE.getMap());
        return JSONUtil.toJsonStr(UrlCacheCenter.INSTANCE.getMap());
    }

}
