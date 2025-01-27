package com.bai.spring.processor;

import com.bai.spring.injector.UrlCacheCenter;
import com.bai.spring.model.InterfaceMethodAnalyse;
import com.bai.spring.injector.MvcUrlInjector;
import com.bai.spring.model.enums.BootClassEnum;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 处理controller
 */
public class ControllerProcessor implements BootClassProcessorService{

    @Override
    public Set<BootClassEnum> getProcessBootClass() {
        return new HashSet<>(Arrays.asList(BootClassEnum.CONTROLLER, BootClassEnum.REST_CONTROLLER));
    }

    @Override
    public void process(ApplicationContext applicationContext, List<Class<?>> clsList, String pluginKey) {
        // 加载进入ioc容器
        clsList.forEach(cls -> {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(cls);
            beanFactory.registerBeanDefinition(cls.getName(), beanDefinitionBuilder.getBeanDefinition());
        });
        // 获取url注入器
        MvcUrlInjector mvcUrlInjector = new MvcUrlInjector(applicationContext);
        // 构建config
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();
        config.setPatternParser(requestMappingHandlerMapping.getPatternParser());
        config.setContentNegotiationManager(requestMappingHandlerMapping.getContentNegotiationManager());
        config.setPathMatcher(requestMappingHandlerMapping.getPathMatcher());
        config.setTrailingSlashMatch(requestMappingHandlerMapping.useTrailingSlashMatch());
        // 执行解析并注入
        clsList.forEach(cls -> {
            // 获取mapping解析
            String[] prePaths = null;
            // 处理mapping
            if(cls.isAnnotationPresent(RequestMapping.class)){
                RequestMapping annotation = cls.getAnnotation(RequestMapping.class);
                prePaths = annotation.value();
            }
            Method[] declaredMethods = cls.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                InterfaceMethodAnalyse analyseInfo = InterfaceMethodAnalyse.analyse(declaredMethod);
                if(analyseInfo == null){
                    continue;
                }
                analyseInfo.buildFinalPath(prePaths);
                analyseInfo.buildMethodRequestInfoMapping(config, declaredMethod, cls, pluginKey);
                // 注入mvc
                mvcUrlInjector.inject(analyseInfo.getMethodRequestInfoMapping());
            }
        });
    }

    @Override
    public void release(ApplicationContext applicationContext, List<Class<?>> clsList, String pluginKey) {
        clsList.forEach(cls -> {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

            // 销毁bean
            beanFactory.destroyBean(cls.getName());
        });
        // 获取url注入器
        MvcUrlInjector mvcUrlInjector = new MvcUrlInjector(applicationContext);
        // 构建config
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();
        config.setPatternParser(requestMappingHandlerMapping.getPatternParser());
        config.setContentNegotiationManager(requestMappingHandlerMapping.getContentNegotiationManager());
        config.setPathMatcher(requestMappingHandlerMapping.getPathMatcher());
        config.setTrailingSlashMatch(requestMappingHandlerMapping.useTrailingSlashMatch());
        // 执行解析并释放
        clsList.forEach(cls -> {
            // 获取mapping解析
            String[] prePaths = null;
            // 处理mapping
            if(cls.isAnnotationPresent(RequestMapping.class)){
                RequestMapping annotation = cls.getAnnotation(RequestMapping.class);
                prePaths = annotation.value();
            }
            Method[] declaredMethods = cls.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                InterfaceMethodAnalyse analyseInfo = InterfaceMethodAnalyse.analyse(declaredMethod);
                if(analyseInfo == null){
                    continue;
                }
                // 构建最终地址
                analyseInfo.buildFinalPath(prePaths);
                analyseInfo.buildMethodRequestInfoMapping(config, declaredMethod, cls, pluginKey);
                // 注入mvc
                mvcUrlInjector.release(analyseInfo.getMethodRequestInfoMapping());
            }
        });
    }
}
