package com.bai.spring.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class InterfaceMethodAnalyse {

    // 请求方法类型
    private RequestMethod[] requestMethod;
    // mapping地址
    private String[] paths;
    // 最终地址
    private String[] finalPaths;
    // headers
    private String[] headers;
    // 参数
    private String[] params;
    // 是否覆盖url(默认不覆盖)
    private Boolean cover = false;
    // 方法请求映射
    private MethodRequestInfoMapping methodRequestInfoMapping;

    public void buildMethodRequestInfoMapping(RequestMappingInfo.BuilderConfiguration config, Method method, Class<?> cls){
        Assert.notEmpty(finalPaths, "finalPaths is empty");
        Assert.notEmpty(requestMethod, "requestMethod is empty");
        Assert.notNull(method, "method is null");
        Assert.notNull(config, "RequestMappingInfo.BuilderConfiguration is null");
        methodRequestInfoMapping = MethodRequestInfoMapping.builder()
                .method(method)
                .cls(cls)
                .requestMappingInfo(
                        RequestMappingInfo
                                .paths(finalPaths)
                                .options(config)
                                .methods(requestMethod)
                                .params(params)
                                .headers(headers)
                                .build()
                ).build();
    }

    public void buildFinalPath(String[] prePaths){
        List<String> finalPaths = new ArrayList<>();

        if(paths == null || paths.length == 0){
            this.setFinalPaths(prePaths);
            return;
        }

        if(prePaths == null || prePaths.length == 0){
            this.setFinalPaths(paths);
            return;
        }

        for (String prePath : prePaths) {
            for (String path : paths) {
                finalPaths.add((prePath.endsWith("/") ? prePath : prePath + "/") + (path.startsWith("/") ? path.substring(1) : path));
            }
        }
        this.setFinalPaths(finalPaths.toArray(new String[0]));
    }

    public static InterfaceMethodAnalyse analyse(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return analyse(requestMapping);
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return analyse(getMapping);
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            return analyse(putMapping);
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            return analyse(deleteMapping);
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return analyse(postMapping);
        }
        return null;
    }

    protected static InterfaceMethodAnalyse analyse(RequestMapping requestMapping) {
        return InterfaceMethodAnalyse.builder()
                .requestMethod(requestMapping.method())
                .paths(requestMapping.value())
                .headers(requestMapping.headers())
                .params(requestMapping.params())
                .build();
    }

    protected static InterfaceMethodAnalyse analyse(GetMapping getMapping) {
        return InterfaceMethodAnalyse.builder()
                .requestMethod(new RequestMethod[]{RequestMethod.GET})
                .paths(getMapping.value())
                .headers(getMapping.headers())
                .params(getMapping.params())
                .build();
    }

    protected static InterfaceMethodAnalyse analyse(PutMapping putMapping) {
        return InterfaceMethodAnalyse.builder()
                .requestMethod(new RequestMethod[]{RequestMethod.PUT})
                .paths(putMapping.value())
                .headers(putMapping.headers())
                .params(putMapping.params())
                .build();
    }

    protected static InterfaceMethodAnalyse analyse(DeleteMapping deleteMapping) {
        return InterfaceMethodAnalyse.builder()
                .requestMethod(new RequestMethod[]{RequestMethod.DELETE})
                .paths(deleteMapping.value())
                .headers(deleteMapping.headers())
                .params(deleteMapping.params())
                .build();
    }

    protected static InterfaceMethodAnalyse analyse(PostMapping postMapping) {
        return InterfaceMethodAnalyse.builder()
                .requestMethod(new RequestMethod[]{RequestMethod.POST})
                .paths(postMapping.value())
                .headers(postMapping.headers())
                .params(postMapping.params())
                .build();
    }

    public Class<?> getCls(){
        if(methodRequestInfoMapping == null){
            return null;
        }
        return methodRequestInfoMapping.getCls();
    }

    public Method getMethod(){
        if(methodRequestInfoMapping == null){
            return null;
        }
        return methodRequestInfoMapping.getMethod();
    }

    public RequestMappingInfo getRequestMappingInfo(){
        if(methodRequestInfoMapping == null){
            return null;
        }
        return methodRequestInfoMapping.getRequestMappingInfo();
    }

}


