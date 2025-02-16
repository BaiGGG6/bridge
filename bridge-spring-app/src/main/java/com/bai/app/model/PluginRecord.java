package com.bai.app.model;


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

    private String sign;

    private String version;

    private PluginStatus status;

    private LocalDateTime updateTime;

    public static PluginRecord buildByPluginMeta(PluginMeta pluginMeta, PluginStatus pluginStatus){
        return new PluginRecord(pluginMeta.getSign(), pluginMeta.getVersion(), pluginStatus, LocalDateTime.now());
    }

    public String getKey(){
        return sign + BridgeCoreConstants.SEPARATE + version;
    }

}
