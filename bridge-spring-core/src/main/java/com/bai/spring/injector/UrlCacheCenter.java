package com.bai.spring.injector;

import com.bai.bridge.Exception.PluginException;
import com.bai.bridge.base.BridgeCoreConstants;
import com.bai.bridge.base.SpiFactory;
import com.bai.spring.BridgeSpringConstants;
import com.bai.spring.model.InterfaceMethodAnalyse;
import com.bai.spring.model.MethodRequestInfoMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public enum UrlCacheCenter {
    INSTANCE;

    // key = url:method value=List<MethodRequestInfoMapping> 缓存所有的url
    private final Map<String, CopyOnWriteArrayList<MethodRequestInfoMapping>> urlMethodMap = new ConcurrentHashMap<>();

    private RequestMappingHandlerMapping handlerMapping;

    private ApplicationContext applicationContext;

    public Map getMap() {
        return urlMethodMap;
    }

    public void init(ApplicationContext context) {
        handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        applicationContext = context;
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod value = entry.getValue();
            RequestMappingInfo requestMappingInfo = entry.getKey();
            for (String patternValue : requestMappingInfo.getPatternValues()) {
                for (RequestMethod method : requestMappingInfo.getMethodsCondition().getMethods()) {
                    String key = method.name() + BridgeCoreConstants.SEPARATE + patternValue;
                    MethodRequestInfoMapping methodRequestInfoMapping = new MethodRequestInfoMapping(BridgeSpringConstants.MAIN_APP_CONTEXT_PLUGIN_KEY, value.getBeanType(), value.getMethod(), entry.getKey());
                    urlMethodMap.put(key, new CopyOnWriteArrayList<>(Collections.singletonList(methodRequestInfoMapping)));
                }
            }
        }
    }

    public void loadUrl(MethodRequestInfoMapping methodRequestInfoMapping) {
        RequestMappingInfo requestMappingInfo = methodRequestInfoMapping.getRequestMappingInfo();
        Optional<String> path = requestMappingInfo.getPatternValues().stream().findFirst();
        Optional<RequestMethod> requestMethod = requestMappingInfo.getMethodsCondition().getMethods().stream().findFirst();
        if (!requestMethod.isPresent() || !path.isPresent()) {
            throw new PluginException("请求方法或者地址不存在");
        }
        String key = requestMethod.get().name() + BridgeCoreConstants.SEPARATE + path.get();
        CopyOnWriteArrayList<MethodRequestInfoMapping> mappingList = urlMethodMap.getOrDefault(key, new CopyOnWriteArrayList<>());
        mappingList.add(methodRequestInfoMapping);
        urlMethodMap.put(key, mappingList);
    }

    public void unloadUrl(MethodRequestInfoMapping methodRequestInfoMapping) {
        RequestMappingInfo requestMappingInfo = methodRequestInfoMapping.getRequestMappingInfo();
        Optional<String> path = requestMappingInfo.getPatternValues().stream().findFirst();
        Optional<RequestMethod> requestMethod = requestMappingInfo.getMethodsCondition().getMethods().stream().findFirst();
        if (!requestMethod.isPresent() || !path.isPresent()) {
            throw new PluginException("请求方法或者地址不存在");
        }
        String key = requestMethod.get().name() + BridgeCoreConstants.SEPARATE + path.get();
        List<MethodRequestInfoMapping> mappingList = urlMethodMap.get(key);
        if (mappingList == null || mappingList.isEmpty()) {
            return;
        }
        mappingList.remove(methodRequestInfoMapping);
        if (mappingList.isEmpty()) {
            urlMethodMap.remove(key);
        }
    }

    /**
     * 获取这个RequestInfo 所有对应的url和method
     *
     * @param methodRequestInfoMapping
     * @return
     */
    public MethodRequestInfoMapping getLastMethodRequestInfoMapping(MethodRequestInfoMapping methodRequestInfoMapping) {
        Optional<String> path = methodRequestInfoMapping.getRequestMappingInfo().getPatternValues().stream().findFirst();
        Optional<RequestMethod> requestMethod = methodRequestInfoMapping.getRequestMappingInfo().getMethodsCondition().getMethods().stream().findFirst();
        if(!path.isPresent() || !requestMethod.isPresent()){
            throw new PluginException("请求方法或者地址不存在");
        }
        return getLastMethodRequestInfoMapping(path.get(), requestMethod.get());
    }

    /**
     * 获取当前url:requestMethod 最新的 MethodRequestInfoMapping
     *
     * @param url
     * @param requestMethod
     * @return
     */
    private MethodRequestInfoMapping getLastMethodRequestInfoMapping(String url, RequestMethod requestMethod) {
        String key = requestMethod.name() + BridgeCoreConstants.SEPARATE + url;
        List<MethodRequestInfoMapping> methodRequestInfoMappings = urlMethodMap.get(key);
        if (methodRequestInfoMappings == null) {
            return null;
        }
        CopyOnWriteArrayList<MethodRequestInfoMapping> mappingList = urlMethodMap.get(key);
        return mappingList.get(mappingList.size() - 1);
    }

//    public static void main(String[] args) throws NoSuchMethodException {
//        Method init = SpiFactory.class.getDeclaredMethod("init");
//        RequestMappingInfo build = RequestMappingInfo.paths(new String("/adasd")).methods(new RequestMethod[]{RequestMethod.GET}).build();
//        UrlCacheCenter.INSTANCE.loadUrl(new MethodRequestInfoMapping(Integer.class, SpiFactory.class.getDeclaredMethod("init"), build));
//        System.out.println(UrlCacheCenter.INSTANCE.getMap());
//        UrlCacheCenter.INSTANCE.unloadUrl(new MethodRequestInfoMapping(Integer.class, SpiFactory.class.getDeclaredMethod("init"), build));
//        System.out.println(UrlCacheCenter.INSTANCE.getMap());
//    }


}
