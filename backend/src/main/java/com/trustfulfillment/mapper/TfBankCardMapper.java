package com.trustfulfillment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trustfulfillment.entity.TfBankCard;
import org.apache.ibatis.annotations.Mapper;

/**
 * 银行卡Mapper
 */
@Mapper
public interface TfBankCardMapper extends BaseMapper<TfBankCard> {
}
