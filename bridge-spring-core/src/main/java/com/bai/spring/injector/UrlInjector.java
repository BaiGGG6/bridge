package com.bai.spring.injector;

import com.bai.spring.model.InterfaceMethodAnalyse;

public interface UrlInjector {

    void inject(InterfaceMethodAnalyse interfaceMethodAnalyse);

    void release(InterfaceMethodAnalyse interfaceMethodAnalyse);

}
