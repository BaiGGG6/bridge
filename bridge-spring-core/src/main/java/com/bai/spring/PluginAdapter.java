package com.bai.spring;

import com.bai.bridge.base.BridgeCoreConstants;
import com.bai.bridge.model.PluginMeta;
import com.bai.spring.context.BridgeApplicationContext;
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

    private ApplicationContext applicationContext;

    // 存储插件所使用的applicationContext
    private final Map<String, ApplicationContext> applicationContextMap = new HashMap<>();

    // 初始化
    public void initPluginAdapter(ApplicationContext context) {
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
        // 存入缓存
        applicationContextMap.put(buildKey(pluginMeta.getSign(), pluginMeta.getVersion()), applicationContext);
        // 获取所有类型的执行器
        for (BootClassEnum value : BootClassEnum.values()) {
            BootClassProcessorService processor = BootClassProcessorFactory.INSTANCE.getProcessor(value);
            // 进行加载
            processor.process(currentAppContext, clsMap.getOrDefault(value, new ArrayList<>()));
        }
    }

    public void release(PluginMeta pluginMeta, Map<BootClassEnum, List<Class<?>>> clsMap) {
        // 获取对应的上下文进行数据清空
        ApplicationContext appContext = applicationContextMap.get(buildKey(pluginMeta.getSign(), pluginMeta.getVersion()));
        // 若为空，则是未加载
        if(appContext == null){
            return;
        }
        // 获取所有类型
        for (BootClassEnum value : BootClassEnum.values()) {
            BootClassProcessorService processor = BootClassProcessorFactory.INSTANCE.getProcessor(value);
            // 进行加载
            processor.release(appContext, clsMap.getOrDefault(value, new ArrayList<>()));
        }
    }

}
