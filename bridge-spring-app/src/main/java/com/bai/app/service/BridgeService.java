package com.bai.app.service;

import com.bai.app.model.PluginRecord;
import com.bai.bridge.model.PluginMeta;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BridgeService {

    PluginMeta loadPlugin(PluginRecord pluginRecord);

    Boolean releasePlugin(PluginRecord pluginRecord);

    PluginMeta uploadPlugin(MultipartFile pluginFile);

    List<PluginRecord> listPlugin();
}
