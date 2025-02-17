package com.bai.app.controller;

import com.bai.app.model.PluginRecord;
import com.bai.app.service.BridgeService;
import com.bai.bridge.Exception.PluginException;
import com.bai.bridge.model.PluginMeta;
import lombok.Cleanup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/bridge/plugin")
public class BridgeController {

    @Autowired
    private BridgeService bridgeService;

    /**
     * 获取所有的插件
     * @return
     */
    @GetMapping("/list")
    public List<PluginRecord> listPlugin(){
        return bridgeService.listPlugin();
    }

    /**
     * 上传插件
     * @param pluginFile
     * @return
     */
    @PostMapping("/upload")
    public PluginMeta uploadPlugin(@RequestParam("pluginFile") MultipartFile pluginFile){
        return bridgeService.uploadPlugin(pluginFile);
    }

    /**
     * 加载插件
     * @param pluginRecord
     * @return
     */
    @PostMapping("/load")
    public PluginMeta loadPlugin(@RequestBody PluginRecord pluginRecord){
        return bridgeService.loadPlugin(pluginRecord);
    }

    /**
     * 释放插件
     * @param pluginRecord
     * @return
     */
    @PostMapping("/release")
    public Boolean releasePlugin(@RequestBody PluginRecord pluginRecord){
        return bridgeService.releasePlugin(pluginRecord);
    }

    /**
     * 删除插件
     * @param pluginRecord
     * @return
     */
    @DeleteMapping("/delete")
    public Boolean deletePlugin(@RequestBody PluginRecord pluginRecord){
        return bridgeService.deletePlugin(pluginRecord);
    }






}
