package com.bai.spring.injector;

import com.bai.spring.model.InterfaceMethodAnalyse;
import com.bai.spring.model.MethodRequestInfoMapping;

public interface UrlInjector {

    void inject(MethodRequestInfoMapping methodRequestInfoMapping);

    void release(MethodRequestInfoMapping methodRequestInfoMapping);

}
