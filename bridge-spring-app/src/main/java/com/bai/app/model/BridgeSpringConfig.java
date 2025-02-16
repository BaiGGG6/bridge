package com.bai.app.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
public class BridgeSpringConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 没有运用minio，插件存储地址
     */
    @Value("${bridge.pluginFilePath:pluginFiles}")
    private String pluginFilePath;

    /**
     * 冷加载的插件存储地址
     */
    @Value("${bridge.pluginStarterFilePath:demo}")
    private String pluginStarterFilePath;

}
