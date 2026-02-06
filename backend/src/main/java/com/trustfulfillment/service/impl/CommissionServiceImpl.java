package com.trustfulfillment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.TfCommission;
import com.trustfulfillment.entity.TfMerchant;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.mapper.TfCommissionMapper;
import com.trustfulfillment.mapper.TfMerchantMapper;
import com.trustfulfillment.service.CommissionService;
import com.trustfulfillment.service.ConfigService;
import com.trustfulfillment.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 提成服务实现
 */
@Service
public class CommissionServiceImpl implements CommissionService {

    @Autowired
    private TfCommissionMapper commissionMapper;
    
    @Autowired
    private TfMerchantMapper merchantMapper;
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private ConfigService configService;

    @Override
    public TfCommission calculateAndDeductCommission(TfOrder order, BigDecimal releaseAmount,
            Long stageId, Integer stageType, String remark) {
        
        // 获取商户提成比例
        BigDecimal commissionRate = getMerchantCommissionRate(order.getSellerId());
        
        // 计算提成金额（保留2位小数，四舍五入）
        BigDecimal commissionAmount = releaseAmount.multiply(commissionRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // 商家实际到账金额
        BigDecimal actualAmount = releaseAmount.subtract(commissionAmount);
        
        // 获取商户信息
        SysUser seller = userService.getById(order.getSellerId());
        Long merchantId = seller != null ? seller.getMerchantId() : null;
        
        // 创建提成记录
        TfCommission commission = new TfCommission();
        commission.setOrderId(order.getId());
        commission.setMerchantId(merchantId);
        commission.setMerchantUserId(order.getSellerId());
        commission.setStageId(stageId);
        commission.setStageType(stageType);
        commission.setOriginalAmount(releaseAmount);
        commission.setCommissionRate(commissionRate);
        commission.setCommissionAmount(commissionAmount);
        commission.setActualAmount(actualAmount);
        commission.setOrderNo(order.getOrderNo());
        commission.setOrderTitle(order.getTitle());
        commission.setRemark(remark);
        commission.setStatus(1); // 已结算
        
        // 保存提成记录
        commissionMapper.insert(commission);
        
        return commission;
    }

    @Override
    public BigDecimal getMerchantCommissionRate(Long merchantUserId) {
        // 1. 首先获取用户信息，取得商户ID
        SysUser user = userService.getById(merchantUserId);
        if (user == null || user.getMerchantId() == null) {
            // 没有商户信息，使用系统默认提成比例
            return configService.getDecimal("default_commission_rate", new BigDecimal("5.00"));
        }
        
        // 2. 查询商户信息获取提成比例
        TfMerchant merchant = merchantMapper.selectById(user.getMerchantId());
        if (merchant == null || merchant.getCommissionRate() == null) {
            // 商户没有设置提成比例，使用系统默认
            return configService.getDecimal("default_commission_rate", new BigDecimal("5.00"));
        }
        
        return merchant.getCommissionRate();
    }

    @Override
    public IPage<TfCommission> getCommissionList(int page, int size, Long merchantId, Long orderId) {
        LambdaQueryWrapper<TfCommission> wrapper = new LambdaQueryWrapper<>();
        
        if (merchantId != null) {
            wrapper.eq(TfCommission::getMerchantId, merchantId);
        }
        if (orderId != null) {
            wrapper.eq(TfCommission::getOrderId, orderId);
        }
        
        wrapper.orderByDesc(TfCommission::getCreateTime);
        
        IPage<TfCommission> pageResult = commissionMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 填充商户名称
        for (TfCommission commission : pageResult.getRecords()) {
            if (commission.getMerchantId() != null) {
                TfMerchant merchant = merchantMapper.selectById(commission.getMerchantId());
                if (merchant != null) {
                    commission.setShopName(merchant.getShopName());
                    commission.setMerchantName(merchant.getCompanyName() != null ? 
                            merchant.getCompanyName() : merchant.getLegalPerson());
                }
            }
            if (commission.getMerchantUserId() != null && commission.getMerchantName() == null) {
                SysUser user = userService.getById(commission.getMerchantUserId());
                if (user != null) {
                    commission.setMerchantName(user.getNickname());
                }
            }
        }
        
        return pageResult;
    }

    @Override
    public BigDecimal getTotalCommission() {
        LambdaQueryWrapper<TfCommission> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TfCommission::getCommissionAmount);
        
        return commissionMapper.selectList(wrapper).stream()
                .map(TfCommission::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getMonthCommission(int year, int month) {
        return commissionMapper.selectMonthCommission(year, month);
    }
}
