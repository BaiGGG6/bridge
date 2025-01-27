package com.bai.spring;

import com.bai.bridge.base.BridgeCoreConstants;
import com.bai.bridge.model.PluginMeta;
import com.bai.spring.context.BridgeApplicationContext;
import com.bai.spring.context.ContextCacheCenter;
import com.bai.spring.model.enums.BootClassEnum;
import com.bai.bridge.model.enums.SpaceMode;
import com.bai.spring.processor.BootClassProcessorFactory;
import com.bai.spring.processor.BootClassProcessorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public enum PluginAdapter {
    INSTANCE;

    // 主程序的上下文
    private ApplicationContext applicationContext;

    // 初始化
    public void init(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * 构建key sign:version
     * @param sign
     * @param version
     * @return
     */
    private static String buildKey(String sign, String version){
        return sign + BridgeCoreConstants.SEPARATE + version;
    }

    /**
     * 选择上下文
     * @param spaceMode
     * @return
     */
    public ApplicationContext choseApplicationContext(SpaceMode spaceMode){
        ApplicationContext resultContext = applicationContext;
        if(spaceMode.equals(SpaceMode.ISOLATION)){
            // 若是隔离模式则自建一个上下文空间
            BridgeApplicationContext bridgeApplicationContext = new BridgeApplicationContext();
            bridgeApplicationContext.setParent(applicationContext);
            bridgeApplicationContext.refresh();
            resultContext = bridgeApplicationContext;
        }
        return resultContext;
    }

    public void process(PluginMeta pluginMeta, Map<BootClassEnum, List<Class<?>>> clsMap) {
        // 获取配置里上下文隔离配置，选择对应的上下文
        ApplicationContext currentAppContext = choseApplicationContext(pluginMeta.getSpaceMode());
        String pluginKey = buildKey(pluginMeta.getSign(), pluginMeta.getVersion());
        // 存入缓存
        ContextCacheCenter.INSTANCE.landingContext(pluginKey, currentAppContext);
        // 获取所有类型的执行器
        for (BootClassEnum value : BootClassEnum.values()) {
            BootClassProcessorService processor = BootClassProcessorFactory.INSTANCE.getProcessor(value);
            // 进行加载
            processor.process(currentAppContext, clsMap.getOrDefault(value, new ArrayList<>()), pluginKey);
        }
    }

    public void release(PluginMeta pluginMeta, Map<BootClassEnum, List<Class<?>>> clsMap) {
        String pluginKey = buildKey(pluginMeta.getSign(), pluginMeta.getVersion());
        // 获取对应的上下文进行数据清空
        ApplicationContext appContext = ContextCacheCenter.INSTANCE.getContext(pluginKey);
        // 若为空，则是未加载
        if(appContext == null){
            return;
        }
        // 获取所有类型
        for (BootClassEnum value : BootClassEnum.values()) {
            BootClassProcessorService processor = BootClassProcessorFactory.INSTANCE.getProcessor(value);
            // 进行加载
            processor.release(appContext, clsMap.getOrDefault(value, new ArrayList<>()), pluginKey);
        }
        // 从上下文中心释放
        ContextCacheCenter.INSTANCE.releaseContext(pluginKey);
    }

}
