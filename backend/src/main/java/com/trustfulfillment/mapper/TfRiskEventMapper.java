package com.trustfulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trustfulfillment.entity.TfRiskEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface TfRiskEventMapper extends BaseMapper<TfRiskEvent> {
    
    /**
     * 统计指定时间范围内的风险事件数
     */
    @Select("SELECT COUNT(*) FROM tf_risk_event WHERE create_time >= #{startTime} AND create_time < #{endTime}")
    Long countByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内指定状态的事件数
     */
    @Select("SELECT COUNT(*) FROM tf_risk_event WHERE status = #{status} AND create_time >= #{startTime} AND create_time < #{endTime}")
    Long countByStatusAndDateRange(@Param("status") Integer status, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内指定等级的事件数
     */
    @Select("SELECT COUNT(*) FROM tf_risk_event WHERE risk_level = #{level} AND create_time >= #{startTime} AND create_time < #{endTime}")
    Long countByLevelAndDateRange(@Param("level") String level, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计用户在指定时间范围内的操作次数
     */
    @Select("SELECT COUNT(*) FROM tf_risk_event WHERE user_id = #{userId} AND event_type = #{eventType} AND create_time >= #{startTime}")
    Long countUserOperations(@Param("userId") Long userId, @Param("eventType") String eventType, @Param("startTime") LocalDateTime startTime);
}
