package com.bai.spring.processor;

import com.bai.bridge.model.PluginMeta;
import com.bai.spring.model.enums.BootClassEnum;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;

public interface BootClassProcessorService {

    Set<BootClassEnum> getProcessBootClass();

    void process(ApplicationContext applicationContext, List<Class<?>> clsList, PluginMeta pluginMeta);
    
    void release(ApplicationContext applicationContext, List<Class<?>> clsList, PluginMeta pluginMeta);

}
