package com.bai.spring.injector;

import cn.hutool.json.JSONUtil;
import com.bai.bridge.Exception.PluginException;
import com.bai.spring.context.ContextCacheCenter;
import com.bai.spring.model.InterfaceMethodAnalyse;
import com.bai.spring.model.MethodRequestInfoMapping;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class MvcUrlInjector implements UrlInjector {

    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private ApplicationContext applicationContext;

    private Boolean cover;

    public MvcUrlInjector(ApplicationContext context, Boolean cover) {
        applicationContext = context;
        requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        requestMappingHandlerAdapter = context.getBean(RequestMappingHandlerAdapter.class);
        this.cover = cover;
    }

    @Override
    public void inject(MethodRequestInfoMapping methodRequestInfoMapping) {
        if (requestMappingHandlerAdapter == null || requestMappingHandlerMapping == null) {
            throw new PluginException("MvcUrlInjector 还未初始化");
        }
        // 加载进入Url中心
        UrlCacheCenter.INSTANCE.loadUrl(methodRequestInfoMapping);
        if (cover) {
            // 覆盖模式先尝试删除
            requestMappingHandlerMapping.unregisterMapping(methodRequestInfoMapping.getRequestMappingInfo());
            requestMappingHandlerAdapter.afterPropertiesSet();
        }
        log.info("注入url：{}", JSONUtil.toJsonStr(methodRequestInfoMapping));
        Object targetBean = applicationContext.getBean(methodRequestInfoMapping.getCls());
        requestMappingHandlerMapping.registerMapping(methodRequestInfoMapping.getRequestMappingInfo(), targetBean, methodRequestInfoMapping.getMethod());
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
    public void release(MethodRequestInfoMapping methodRequestInfoMapping) {
        // 从url中心卸载
        UrlCacheCenter.INSTANCE.unloadUrl(methodRequestInfoMapping);
        // 此处会卸载所有当前的url
        requestMappingHandlerMapping.unregisterMapping(methodRequestInfoMapping.getRequestMappingInfo());
        requestMappingHandlerAdapter.afterPropertiesSet();
        // 获取对应url最新版本的数据
        MethodRequestInfoMapping lastMethodRequestInfoMapping = UrlCacheCenter.INSTANCE.getLastMethodRequestInfoMapping(methodRequestInfoMapping);
        // 加载之前版本的url
        // 若这个为null则直接跳过
        if (lastMethodRequestInfoMapping == null) {
            return;
        }
        RequestMappingInfo requestMappingInfo = lastMethodRequestInfoMapping.getRequestMappingInfo();
        ApplicationContext context = ContextCacheCenter.INSTANCE.getContext(lastMethodRequestInfoMapping.getPluginKey());
        // 进行装载
        requestMappingHandlerMapping.registerMapping(requestMappingInfo,
                context.getBean(lastMethodRequestInfoMapping.getCls()),
                lastMethodRequestInfoMapping.getMethod());
        requestMappingHandlerAdapter.afterPropertiesSet();
    }


}
