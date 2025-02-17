package com.bai.app.plugin;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.bai.app.model.BridgeAppConstants;
import com.bai.app.model.PluginRecord;
import com.bai.app.model.PluginStatus;
import com.bai.bridge.Exception.PluginException;
import com.bai.bridge.base.BridgeCoreConstants;
import com.bai.bridge.base.DataCacheCenter;
import com.bai.bridge.model.PluginMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class PluginRecordOperator {

    private static ConcurrentMap<String, PluginRecord> pluginRecordMap = new ConcurrentHashMap<>();

    private File pluginRecordsFile = new File(BridgeAppConstants.pluginRecordFilePath);

    // 等待数量
    private AtomicInteger waitCount = new AtomicInteger(0);

    // 最大进入的线程数量
    private static final int MAX_ENTERING_THREADS = 2;

    private Lock persistenceLock = new ReentrantLock();

    @Autowired
    @Qualifier("BridgeThreadPool")
    private Executor bridgeThreadPool;

    @PostConstruct
    private void init(){
        if(!pluginRecordsFile.exists()){
            try {
                File parentFile = new File(pluginRecordsFile.getParent());
                if(!parentFile.exists()){
                    parentFile.mkdirs();
                }
                if(!pluginRecordsFile.exists()){
                    pluginRecordsFile.createNewFile();
                }
            } catch (IOException e) {
                throw new PluginException("创建插件持久化文件失败");
            }
            return;
        }
        String recordsJson = FileUtil.readUtf8String(pluginRecordsFile);
        if(!StringUtils.hasText(recordsJson)){
            return;
        }
        List<PluginRecord> list = JSONUtil.toList(recordsJson, PluginRecord.class);
        for (PluginRecord pluginRecord : list) {
            pluginRecordMap.put(pluginRecord.getSign() + BridgeCoreConstants.SEPARATE + pluginRecord.getVersion(), pluginRecord);
        }
    }

    public static List<PluginRecord> getPluginRecords(){
        return new ArrayList<>(pluginRecordMap.values());
    }

    public PluginRecord findById(String id){
        for (PluginRecord value : pluginRecordMap.values()) {
            if(value.getId().equals(id)){
                return value;
            }
        }
        return null;
    }

    public PluginRecord findBySignAndVersion(String sign, String version){
        return pluginRecordMap.get(sign + BridgeCoreConstants.SEPARATE + version);
    }

    public void savePluginRecord(PluginRecord pluginRecord){
        pluginRecord.setCreateTime(LocalDateTime.now());
        pluginRecordMap.put(pluginRecord.getKey(), pluginRecord);
        persistenceRecords();
    }

    public void updatePluginRecord(PluginRecord pluginRecord){
        PluginRecord savePluginRecord = pluginRecordMap.get(pluginRecord.getKey());
        savePluginRecord.setStatus(pluginRecord.getStatus());
        savePluginRecord.setUpdateTime(pluginRecord.getUpdateTime());
        pluginRecordMap.put(pluginRecord.getKey(), savePluginRecord);
        persistenceRecords();
    }

    public void deletePluginRecord(String sign, String version){
        pluginRecordMap.remove(sign + BridgeCoreConstants.SEPARATE + version);
        persistenceRecords();
    }

    public void deletePluginRecord(String id){
        for (PluginRecord value : pluginRecordMap.values()) {
            if(value.getId().equals(id)){
                pluginRecordMap.remove(value.getKey());
                break;
            }
        }
        persistenceRecords();
    }

    public void persistenceRecords(){
        bridgeThreadPool.execute(() -> {

            boolean canWait;
            synchronized (PluginRecordOperator.class) {
                // 检查是否可以等待
                canWait = waitCount.get() < MAX_ENTERING_THREADS;
                if (canWait) {
                    waitCount.addAndGet(1);
                }
            }

            // 超出等待数量
            if(!canWait){
                return;
            }

            // 加锁
            persistenceLock.lock();
            try {
                // 进行持久化
                List<PluginRecord> pluginRecords = getPluginRecords();
                String jsonStr = JSONUtil.toJsonStr(pluginRecords);
                FileUtil.writeUtf8String(jsonStr, pluginRecordsFile);

            }catch (Exception | Error e){
                log.error("持久化插件数据失败");
            }finally {
                // 解锁
                persistenceLock.unlock();
                // 递减
                waitCount.decrementAndGet();
            }
        });


    }

}
