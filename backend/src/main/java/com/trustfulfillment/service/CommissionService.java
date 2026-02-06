package com.trustfulfillment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trustfulfillment.entity.TfCommission;
import com.trustfulfillment.entity.TfOrder;

import java.math.BigDecimal;

/**
 * 提成服务接口
 */
public interface CommissionService {
    
    /**
     * 计算并扣除提成
     * 在资金释放时调用，计算平台提成并返回商家实际到账金额
     * 
     * @param order 订单信息
     * @param releaseAmount 释放金额
     * @param stageId 阶段ID（可为null）
     * @param stageType 阶段类型: 1首付款 2尾款 3质保款
     * @param remark 备注
     * @return 提成记录（包含提成金额和实际到账金额）
     */
    TfCommission calculateAndDeductCommission(TfOrder order, BigDecimal releaseAmount, 
            Long stageId, Integer stageType, String remark);
    
    /**
     * 获取商户的提成比例
     * @param merchantUserId 商户用户ID
     * @return 提成比例（百分比，如8.00表示8%）
     */
    BigDecimal getMerchantCommissionRate(Long merchantUserId);
    
    /**
     * 分页查询提成记录
     */
    IPage<TfCommission> getCommissionList(int page, int size, Long merchantId, Long orderId);
    
    /**
     * 获取平台总提成统计
     */
    BigDecimal getTotalCommission();
    
    /**
     * 获取指定月份的提成统计
     */
    BigDecimal getMonthCommission(int year, int month);
}
