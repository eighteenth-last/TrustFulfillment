package com.trustfulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trustfulfillment.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统配置Mapper
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据配置键获取配置值
     */
    @Select("SELECT config_value FROM sys_config WHERE config_key = #{key}")
    String getValueByKey(@Param("key") String key);
}
