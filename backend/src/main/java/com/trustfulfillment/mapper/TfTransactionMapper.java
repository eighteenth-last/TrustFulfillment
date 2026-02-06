package com.trustfulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trustfulfillment.entity.TfTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper
public interface TfTransactionMapper extends BaseMapper<TfTransaction> {
    
    /**
     * 查询今日流入金额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM tf_transaction WHERE type IN ('deposit', 'recharge') AND status = 1 AND DATE(create_time) = CURDATE()")
    BigDecimal selectTodayInflow();
    
    /**
     * 查询指定月份的交易金额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM tf_transaction WHERE status = 1 AND YEAR(create_time) = #{year} AND MONTH(create_time) = #{month}")
    BigDecimal selectMonthAmount(@Param("year") int year, @Param("month") int month);
    
    /**
     * 按类型和时间范围查询交易金额总和
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM tf_transaction WHERE type = #{type} AND create_time >= #{startTime} AND create_time < #{endTime}")
    BigDecimal selectSumByTypeAndDateRange(@Param("type") Integer type, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询时间范围内的提现手续费
     */
    @Select("SELECT COALESCE(SUM(ABS(amount) * 0.01), 0) FROM tf_transaction WHERE type = 2 AND create_time >= #{startTime} AND create_time < #{endTime}")
    BigDecimal selectWithdrawFeeByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
