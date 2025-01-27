package com.bai.spring.context;

import com.bai.bridge.Exception.PluginException;
import com.bai.spring.BridgeSpringConstants;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ContextCacheCenter {
    INSTANCE;

    private ApplicationContext applicationContext;

    // 存储插件所使用的applicationContext
    private final Map<String, ApplicationContext> applicationContextMap = new ConcurrentHashMap<>();

    public void init(ApplicationContext context) {
        applicationContext = context;
    }

    public void landingContext(String pluginKey, ApplicationContext context) {
        if (pluginKey.equals(BridgeSpringConstants.MAIN_APP_CONTEXT_PLUGIN_KEY)) {
            throw new PluginException("不被允许进行存储MAIN_APP");
        }
        applicationContextMap.put(pluginKey, context);
    }

    public void releaseContext(String pluginKey) {
        if (pluginKey.equals(BridgeSpringConstants.MAIN_APP_CONTEXT_PLUGIN_KEY)) {
            throw new PluginException("不被允许进行释放MAIN_APP");
        }
        applicationContextMap.remove(pluginKey);
    }

    public ApplicationContext getContext(String pluginKey) {
        if (pluginKey.equals(BridgeSpringConstants.MAIN_APP_CONTEXT_PLUGIN_KEY)) {
            return applicationContext;
        }
        return applicationContextMap.get(pluginKey);
    }

}
