package com.bai.spring.processor;

import com.bai.spring.model.enums.BootClassEnum;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 处理bean
 */
public class ComponentProcessor implements BootClassProcessorService{

    @Override
    public Set<BootClassEnum> getProcessBootClass() {
        return new HashSet<>(Arrays.asList(BootClassEnum.COMPONENT, BootClassEnum.BEAN, BootClassEnum.SERVICE, BootClassEnum.REPOSITORY));
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
    }

    @Override
    public void release(ApplicationContext applicationContext, List<Class<?>> clsList, String pluginKey) {
        clsList.forEach(cls -> {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

            // 销毁bean
            beanFactory.destroyBean(cls.getName());
        });
    }
}
