package com.bai.app.model;


import cn.hutool.core.lang.UUID;
import com.bai.bridge.base.BridgeCoreConstants;
import com.bai.bridge.model.PluginMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 记录运行的插件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginRecord {

    private String id;

    private String sign;

    private String version;

    private PluginStatus status;

    private LocalDateTime updateTime;

    private LocalDateTime createTime;

    public static PluginRecord buildByPluginMeta(PluginMeta pluginMeta, PluginStatus pluginStatus){
        return new PluginRecord(UUID.fastUUID().toString().replaceAll("-", ""), pluginMeta.getSign(), pluginMeta.getVersion(), pluginStatus, LocalDateTime.now(), LocalDateTime.now());
    }

    public String getKey(){
        return sign + BridgeCoreConstants.SEPARATE + version;
    }

}
