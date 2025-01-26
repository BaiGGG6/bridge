package com.bai.spring.injector;

import cn.hutool.json.JSONUtil;
import com.bai.bridge.Exception.PluginException;
import com.bai.spring.model.InterfaceMethodAnalyse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Slf4j
@AllArgsConstructor
public class MvcUrlInjector implements UrlInjector {

    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private ApplicationContext applicationContext;

    private Boolean cover = false;

    public MvcUrlInjector(ApplicationContext context){
        applicationContext = context;
        requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        requestMappingHandlerAdapter = context.getBean(RequestMappingHandlerAdapter.class);
    }

    @Override
    public void inject(InterfaceMethodAnalyse interfaceMethodMappings) {
        if(requestMappingHandlerAdapter == null || requestMappingHandlerMapping == null){
            throw new PluginException("MvcUrlInjector 还未初始化");
        }
        if(cover){
            // 覆盖模式先尝试删除
            requestMappingHandlerMapping.unregisterMapping(interfaceMethodMappings.getRequestMappingInfo());
            requestMappingHandlerAdapter.afterPropertiesSet();
        }
        log.info("注入url：{}", JSONUtil.toJsonStr(interfaceMethodMappings));
        Object targetBean = applicationContext.getBean(interfaceMethodMappings.getCls());
        requestMappingHandlerMapping.registerMapping(interfaceMethodMappings.getRequestMappingInfo(), targetBean, interfaceMethodMappings.getMethod());
        requestMappingHandlerAdapter.afterPropertiesSet();


        // 注入mvc的HandlerMapping
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods =
//                requestMappingHandlerMapping.getHandlerMethods();
//
//        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
//            RequestMappingInfo requestMappingInfo = entry.getKey();
//            HandlerMethod handlerMethod = entry.getValue();
//            System.out.println(JSONUtil.toJsonStr(requestMappingInfo));
//            for (String patternValue : requestMappingInfo.getPatternValues()) {
//
//                if (patternValue.equals()) {
//                    RequestMethod requestMethod =
//                            requestMappingInfo.getMethodsCondition().getMethods().stream().findFirst().get();
//
//                    if (requestMethod.name().equals(putRequestMethod)) {
//                        RequestMappingInfo requestMappingInfoToRegister =
//                                RequestMappingInfo.paths(url[0])
//                                        .methods(requestMethod).params().build();
//
//                        Object handler = handlerMethod.getBean();
//                        requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
//                        requestMappingHandlerMapping.registerMapping(
//                                requestMappingInfoToRegister, handler, handlerMethod.getMethod());
//                    }
//                }
//            }
//        }
//
//        Object bean = applicationContext.getBean(simpleName);
//
//        RequestMappingInfo.BuilderConfiguration builderConfiguration = new RequestMappingInfo.BuilderConfiguration();
//        builderConfiguration.setPatternParser(PathPatternParser.defaultInstance);
//        builderConfiguration.setContentNegotiationManager(new ContentNegotiationManager());
//        RequestMappingInfo requestMappingInfoToRegister =
//                RequestMappingInfo.paths(url[0])
//                        .methods(putRequestMethod).options(builderConfiguration).build();
//        requestMappingHandlerMapping.registerMapping(
//                requestMappingInfoToRegister, bean, method);
//
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods1 =
//                requestMappingHandlerMapping.getHandlerMethods();
//
//        requestMappingHandlerAdapter.afterPropertiesSet();
    }

    @Override
    public void release(InterfaceMethodAnalyse interfaceMethodAnalyse) {
        requestMappingHandlerMapping.unregisterMapping(interfaceMethodAnalyse.getRequestMappingInfo());
        requestMappingHandlerAdapter.afterPropertiesSet();
    }

}
