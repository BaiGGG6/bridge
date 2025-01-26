package com.bai.spring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@Builder
public class MethodRequestInfoMapping {

    // 类
    private Class<?> cls;

    // 方法
    private Method method;

    // Mvc参数所要
    private RequestMappingInfo requestMappingInfo;


}
