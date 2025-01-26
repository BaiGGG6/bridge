package com.bai.spring.injector;

import com.bai.spring.model.InterfaceMethodAnalyse;
import com.bai.spring.model.MethodRequestInfoMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlCacheCenter {

    // key = url:method value=List<MethodRequestInfoMapping> 缓存所有的url
    private static final Map<String, List<MethodRequestInfoMapping>> urlMethodMap = new HashMap<>();

    private static RequestMappingHandlerMapping handlerMapping;

    private static ApplicationContext applicationContext;

    public static void init(ApplicationContext context){
        handlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        applicationContext = context;
        loadUrl();
    }

    public static void loadUrl(){
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod value = entry.getValue();
            MethodRequestInfoMapping methodRequestInfoMapping = new MethodRequestInfoMapping(value.getBeanType(), value.getMethod(), entry.getKey());

        }
    }

    




}
