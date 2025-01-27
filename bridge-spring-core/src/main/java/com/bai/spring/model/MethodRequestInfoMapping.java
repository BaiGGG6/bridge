package com.bai.spring.model;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@Builder
public class MethodRequestInfoMapping {

    // 插件标识
    private String pluginKey;

    // 类
    private Class<?> cls;

    // 方法
    private Method method;

    // Mvc参数所要
    private RequestMappingInfo requestMappingInfo;

}
