package com.bai.app.service.impl;

import cn.hutool.core.io.FileUtil;
import com.bai.app.PluginAppStart;
import com.bai.app.model.PluginRecord;
import com.bai.app.model.PluginStatus;
import com.bai.app.plugin.PluginRecordOperator;
import com.bai.app.service.BridgeService;
import com.bai.bridge.Exception.PluginException;
import com.bai.bridge.PluginProcessor;
import com.bai.bridge.base.BridgeCoreConstants;
import com.bai.bridge.base.DataCacheCenter;
import com.bai.bridge.model.PluginMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static com.bai.app.model.BridgeAppConstants.pluginFoldPath;

@Service
public class BridgeServiceImpl implements BridgeService {

    @Autowired
    private PluginRecordOperator pluginRecordOperator;

    private void checkReady(){
        if(!PluginAppStart.getIsReady()){
            throw new PluginException("初始化插件还会加载完成,请耐心等待");
        }
    }

    @Override
    public PluginMeta uploadPlugin(MultipartFile pluginFile) {
        // 校验是否准备完成
        checkReady();
        if(pluginFile.isEmpty()){
            throw new PluginException("请上传正确的插件");
        }
        PluginMeta pluginMeta = null;
        String tempFoldPath = pluginFoldPath + "/temp";
        // temp文件夹的插件
        File tempFile = new File(tempFoldPath + File.separator + pluginFile.getOriginalFilename());
        try {
            File tempFold = new File(tempFoldPath);
            if(!tempFold.exists()){
                if(!tempFold.mkdirs()){
                    throw new PluginException("创建存储文件夹失败");
                }
            }
            // 转存对应文件夹
            pluginFile.transferTo(tempFile);
            pluginMeta = PluginProcessor.findToBuildPluginMeta(tempFile);
            File finalSaveFile = new File(pluginFoldPath + File.separator + pluginMeta.getSign() + BridgeCoreConstants.SEPARATE + pluginMeta.getVersion() + ".jar");
            FileUtil.move(tempFile, finalSaveFile, true);
            // 存入信息
            pluginRecordOperator.savePluginRecord(PluginRecord.buildByPluginMeta(pluginMeta, PluginStatus.SLEEPING));
        } catch (Exception e) {
            if(pluginMeta != null){
                File file = new File(pluginFoldPath + File.separator + pluginMeta.getSign() + BridgeCoreConstants.SEPARATE + pluginMeta.getVersion() + ".jar");
                file.deleteOnExit();
            }
            // 若存在删除文件
            throw new PluginException("加载插件错误", e);
        } finally {
            tempFile.deleteOnExit();
        }
        return pluginMeta;
    }

    @Override
    public List<PluginRecord> listPlugin() {
        return pluginRecordOperator.getPluginRecords();
    }

    @Override
    public PluginMeta loadPlugin(PluginRecord pluginRecord) {
        // 校验是否准备完成
        checkReady();
        // 校验文件
        if(pluginRecord == null){
            throw new PluginException("请上传插件元数据");
        }
        // 获取对应的插件file
        File pluginFile = new File(pluginFoldPath + File.separator + pluginRecord.getSign() + BridgeCoreConstants.SEPARATE + pluginRecord.getVersion() + ".jar");
        if(!pluginFile.exists()){
            throw new PluginException("插件不存在");
        }
        // 加载插件
        PluginMeta pluginMeta = PluginProcessor.loadJarPlugin(pluginFile);
        // 保存数据
        pluginRecordOperator.updatePluginRecord(PluginRecord.buildByPluginMeta(pluginMeta, PluginStatus.RUNNING));
        return pluginMeta;
    }

    @Override
    public Boolean releasePlugin(PluginRecord pluginRecord) {
        // 校验是否准备完成
        checkReady();
        // 释放插件
        PluginProcessor.releasePlugin(DataCacheCenter.INSTANCE.getPluginMeta(pluginRecord.getSign(), pluginRecord.getVersion()));
        pluginRecord.setStatus(PluginStatus.SLEEPING);
        // 保存数据
        pluginRecordOperator.updatePluginRecord(pluginRecord);
        return true;
    }


}
