package com.bai.app.dao.impl;

import com.bai.app.dao.PluginRecordRepository;
import com.bai.app.model.PluginRecord;
import com.bai.app.plugin.PluginRecordOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository(value = "repository-file")
public class PluginRecordFileRepositoryImpl implements PluginRecordRepository {

    @Autowired
    private PluginRecordOperator operator;

    @Override
    public PluginRecord findById(String id) {
        return operator.findById(id);
    }

    @Override
    public PluginRecord findBySignAndVersion(String sign, String version) {
        return operator.findBySignAndVersion(sign, version);
    }

    @Override
    public boolean savePluginRecord(PluginRecord pluginRecord) {
        operator.savePluginRecord(pluginRecord);
        return true;
    }

    @Override
    public boolean updatePluginRecord(PluginRecord pluginRecord){
        operator.updatePluginRecord(pluginRecord);
        return true;
    }

    @Override
    public boolean deletePluginRecord(String id) {
        operator.deletePluginRecord(id);
        return true;
    }

    @Override
    public boolean deletePluginRecord(String sign, String version) {
        operator.deletePluginRecord(sign, version);
        return true;
    }


}
