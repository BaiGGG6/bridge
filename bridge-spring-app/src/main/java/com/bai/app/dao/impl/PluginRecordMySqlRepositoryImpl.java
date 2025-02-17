package com.bai.app.dao.impl;

import com.bai.app.dao.PluginRecordRepository;
import com.bai.app.mapper.PluginRecordMapper;
import com.bai.app.model.PluginRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository("repository-mysql")
@ConditionalOnClass(name = "org.mybatis.spring.SqlSessionFactoryBean")
@ConditionalOnBean(DataSource.class)
public class PluginRecordMySqlRepositoryImpl implements PluginRecordRepository {

    @Autowired
    private PluginRecordMapper pluginRecordMapper;

    @Override
    public PluginRecord findById(String id) {
        return pluginRecordMapper.findById(id);
    }

    @Override
    public PluginRecord findBySignAndVersion(String sign, String version) {
        return pluginRecordMapper.findBySignAndVersion(sign, version);
    }

    @Override
    public boolean savePluginRecord(PluginRecord pluginRecord) {
        pluginRecordMapper.savePluginRecord(pluginRecord);
        return true;
    }

    @Override
    public boolean updatePluginRecord(PluginRecord pluginRecord) {
        pluginRecordMapper.updatePluginRecord(pluginRecord);
        return true;
    }

    @Override
    public boolean deletePluginRecord(String id) {
        pluginRecordMapper.deletePluginRecord(id);
        return true;
    }

    @Override
    public boolean deletePluginRecord(String sign, String version) {
        pluginRecordMapper.deletePluginRecord(sign, version);
        return true;
    }


}
