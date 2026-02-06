package com.trustfulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trustfulfillment.entity.TfCommission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface TfCommissionMapper extends BaseMapper<TfCommission> {
    
    /**
     * 查询指定时间范围内的总提成金额
     */
    @Select("SELECT COALESCE(SUM(commission_amount), 0) FROM tf_commission WHERE create_time >= #{startTime} AND create_time < #{endTime}")
    BigDecimal selectTotalCommissionByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询指定月份的总提成金额
     */
    @Select("SELECT COALESCE(SUM(commission_amount), 0) FROM tf_commission WHERE YEAR(create_time) = #{year} AND MONTH(create_time) = #{month}")
    BigDecimal selectMonthCommission(@Param("year") int year, @Param("month") int month);
    
    /**
     * 查询商户的累计提成金额
     */
    @Select("SELECT COALESCE(SUM(commission_amount), 0) FROM tf_commission WHERE merchant_id = #{merchantId}")
    BigDecimal selectTotalCommissionByMerchant(@Param("merchantId") Long merchantId);
}
