package com.trustfulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trustfulfillment.entity.TfOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface TfOrderMapper extends BaseMapper<TfOrder> {

    @Select("SELECT COALESCE(SUM(deposit_amount), 0) FROM tf_order WHERE status IN (1,2,3,4,6)")
    BigDecimal selectTotalDepositAmount();

    @Select("SELECT COALESCE(SUM(released_amount), 0) FROM tf_order WHERE status IN (2,3,4)")
    BigDecimal selectTotalReleasedAmount();

    @Select("SELECT COUNT(DISTINCT buyer_id) FROM tf_order")
    Long countDistinctBuyers();
}
