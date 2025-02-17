package com.bai.app.dao;

import com.bai.app.model.PluginRecord;
import com.bai.bridge.model.PluginMeta;

public interface PluginRecordRepository {

    PluginRecord findById(String id);

    PluginRecord findBySignAndVersion(String sign, String version);

    boolean savePluginRecord(PluginRecord pluginRecord);

    boolean updatePluginRecord(PluginRecord pluginRecord);

    boolean deletePluginRecord(String id);

    boolean deletePluginRecord(String sign, String version);

}
