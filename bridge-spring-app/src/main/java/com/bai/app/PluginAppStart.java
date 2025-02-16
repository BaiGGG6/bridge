package com.bai.app;

import com.bai.app.config.ThreadPoolConfig;
import com.bai.app.model.PluginRecord;
import com.bai.app.model.PluginStatus;
import com.bai.app.plugin.PluginRecordOperator;
import com.bai.bridge.PluginProcessor;
import com.bai.bridge.PluginStartService;
import com.bai.bridge.base.BridgeCoreConstants;
import com.bai.bridge.model.PluginMeta;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.bai.app.model.BridgeAppConstants.pluginFoldPath;

/**
 * 当我们的插件环境准备好的回调
 */
@Slf4j
public class PluginAppStart implements PluginStartService {

    @Getter
    private static Boolean isReady = false;

    /**
     * 执行加载
     */
    @Override
    public void execute() {
        // 获取配置文件
        List<PluginRecord> pluginRecords = PluginRecordOperator.getPluginRecords();
        List<CompletableFuture> completableFutures = new ArrayList<>();
        for (PluginRecord pluginRecord : pluginRecords) {
            if(PluginStatus.RUNNING.equals(pluginRecord.getStatus())){
                // 进行加载
                completableFutures.add(CompletableFuture.runAsync(() -> {
                    File pluginFile = new File(pluginFoldPath + File.separator + pluginRecord.getSign() + BridgeCoreConstants.SEPARATE + pluginRecord.getVersion() + ".jar");
                    // 加载插件
                    PluginProcessor.loadJarPlugin(pluginFile);
                }, ThreadPoolConfig.BridgeThreadPool));
            }
        }

        // 等待所有线程执行完
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[1])).join();
        // 修改当前执行好的状态
        isReady = true;
        log.info("启动初始化所有插件完成");
    }

}
