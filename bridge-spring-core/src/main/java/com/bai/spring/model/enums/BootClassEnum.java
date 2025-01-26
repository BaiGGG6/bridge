package com.bai.spring.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;

@Getter
@AllArgsConstructor
public enum BootClassEnum {

    COMPONENT("component"),
    SERVICE("service"),
    CONFIGURATION("configuration"),
    REPOSITORY("repository"),
    BEAN("bean"),
    // Controller放下面，优先加载上面的配置类
    CONTROLLER("controller"),
    REST_CONTROLLER("restController");

    private final String name;

    public static BootClassEnum getClsType(Class<?> cls){
        if(cls.isAnnotationPresent(Controller.class)){
            return BootClassEnum.CONTROLLER;
        }
        if(cls.isAnnotationPresent(RestController.class)){
            return BootClassEnum.REST_CONTROLLER;
        }
        if(cls.isAnnotationPresent(Component.class)){
            return BootClassEnum.COMPONENT;
        }
        if(cls.isAnnotationPresent(Repository.class)){
            return BootClassEnum.REPOSITORY;
        }
        if(cls.isAnnotationPresent(Configuration.class)){
            return BootClassEnum.CONFIGURATION;
        }
        if(cls.isAnnotationPresent(Bean.class)){
            return BootClassEnum.BEAN;
        }
        if(cls.isAnnotationPresent(Service.class)){
            return BootClassEnum.SERVICE;
        }
        return null;
    }


}
