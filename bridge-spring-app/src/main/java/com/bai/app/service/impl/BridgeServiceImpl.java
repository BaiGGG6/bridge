package com.bai.app.service.impl;

import cn.hutool.core.io.FileUtil;
import com.bai.app.PluginAppStart;
import com.bai.app.dao.PluginRecordRepository;
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
import java.util.List;
import java.util.Map;

import static com.bai.app.model.BridgeAppConstants.pluginFoldPath;

@Service
public class BridgeServiceImpl implements BridgeService {

    private PluginRecordRepository pluginRecordRepository;

    @Autowired
    public BridgeServiceImpl(Map<String, PluginRecordRepository> myServices) {
        for (String s : myServices.keySet()) {
            if(!s.equals("repository-file")){
                pluginRecordRepository = myServices.get(s);
                return;
            }
        }
    }

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
        // 记录是否成功加载
        boolean successLoad = false;
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
            // 获取在运行的插件
            Map<String, PluginMeta> signPluginMetaMap = DataCacheCenter.INSTANCE.getSignPluginMetaMap();
            // 校验插件是否在运行
            if(signPluginMetaMap.containsKey(pluginMeta.getPluginKey())){
                throw new PluginException("请先停止运行对应的插件");
            }
            File finalSaveFile = new File(pluginFoldPath + File.separator + pluginMeta.getSign() + BridgeCoreConstants.SEPARATE + pluginMeta.getVersion() + ".jar");
            FileUtil.move(tempFile, finalSaveFile, true);
            // 存入信息
            pluginRecordRepository.savePluginRecord(PluginRecord.buildByPluginMeta(pluginMeta, PluginStatus.SLEEPING));
            successLoad = true;
        } catch (Exception e) {
            if(!successLoad && pluginFile != null){
                File file = new File(pluginFoldPath + File.separator + pluginMeta.getSign() + BridgeCoreConstants.SEPARATE + pluginMeta.getVersion() + ".jar");
                FileUtil.del(file);
            }
            // 若存在删除文件
            throw new PluginException(e.getLocalizedMessage(), e);
        } finally {
            FileUtil.del(tempFile);
        }
        return pluginMeta;
    }

    @Override
    public List<PluginRecord> listPlugin() {
        return PluginRecordOperator.getPluginRecords();
    }

    @Override
    public Boolean deletePlugin(PluginRecord pluginRecord) {
        // 校验是否准备完成
        checkReady();
        // 获取对应的插件file
        File pluginFile = new File(pluginFoldPath + File.separator + pluginRecord.getSign() + BridgeCoreConstants.SEPARATE + pluginRecord.getVersion() + ".jar");
        if(!pluginFile.exists()){
            throw new PluginException("插件不存在");
        }
        // 获取在运行的插件
        Map<String, PluginMeta> signPluginMetaMap = DataCacheCenter.INSTANCE.getSignPluginMetaMap();
        // 校验插件是否在运行
        if(signPluginMetaMap.containsKey(pluginRecord.getKey())){
            throw new PluginException("插件还未停止运行");
        }
        FileUtil.del(pluginFile);
        pluginRecordRepository.deletePluginRecord(pluginRecord.getId());
        return true;
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
        // 获取在运行的插件
        Map<String, PluginMeta> signPluginMetaMap = DataCacheCenter.INSTANCE.getSignPluginMetaMap();
        // 校验插件是否在运行
        if(signPluginMetaMap.containsKey(pluginRecord.getKey())){
            throw new PluginException("插件已经加载");
        }
        // 加载插件
        PluginMeta pluginMeta = PluginProcessor.loadJarPlugin(pluginFile);
        // 保存数据
        pluginRecordRepository.updatePluginRecord(PluginRecord.buildByPluginMeta(pluginMeta, PluginStatus.RUNNING));
        return pluginMeta;
    }

    @Override
    public Boolean releasePlugin(PluginRecord pluginRecord) {
        // 校验是否准备完成
        checkReady();
        PluginMeta pluginMeta = DataCacheCenter.INSTANCE.getPluginMeta(pluginRecord.getSign(), pluginRecord.getVersion());
        if(pluginMeta == null){
            throw new PluginException("插件未在运行");
        }
        // 释放插件
        PluginProcessor.releasePlugin(pluginMeta);
        pluginRecord.setStatus(PluginStatus.SLEEPING);
        // 保存数据
        pluginRecordRepository.updatePluginRecord(pluginRecord);
        return true;
    }


}
