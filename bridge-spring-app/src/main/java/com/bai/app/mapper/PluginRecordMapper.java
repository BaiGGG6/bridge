package com.bai.app.mapper;

import com.bai.app.model.PluginRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import javax.sql.DataSource;
import java.util.List;

@Mapper
@ConditionalOnClass(name = "org.mybatis.spring.SqlSessionFactoryBean")
@ConditionalOnBean(DataSource.class)
public interface PluginRecordMapper {

    @Select("select * from plugin_record")
    List<PluginRecord> listPluginRecord();

    @Select("select * from plugin_record where id = #{id}")
    PluginRecord findById(String id);

    @Select("select * from plugin_record where sign = #{sign} and version = #{version}")
    PluginRecord findBySignAndVersion(String sign, String version);

    @Insert("insert into plugin_record values (#{param.id}, #{param.sign}, #{param.sign}, #{param.status}, #{param.updateTime}, #{param.createTime}) ")
    void savePluginRecord(@Param("param") PluginRecord pluginRecord);

    @Delete("delete from plugin_record where sign = #{sign} and version = #{version}")
    void deletePluginRecord(String sign, String version);

    @Delete("delete from plugin_record where id = #{id}")
    void deletePluginRecord(String id);

    @Update("update plugin_record set status = #{param.status}, update_time = #{param.updateTime}")
    void updatePluginRecord(@Param("param") PluginRecord pluginRecord);

}
