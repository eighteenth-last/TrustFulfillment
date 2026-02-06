package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 平台提成记录实体
 * 记录每次资金释放时从商家收入中扣除的平台提成
 */
@TableName("tf_commission")
public class TfCommission {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;              // 关联订单ID
    private Long merchantId;           // 商户ID
    private Long merchantUserId;       // 商户对应的用户ID
    private Long stageId;              // 关联阶段ID
    private Integer stageType;         // 阶段类型: 1首付款 2尾款 3质保款
    private BigDecimal originalAmount; // 原始金额（释放金额）
    private BigDecimal commissionRate; // 提成比例（百分比）
    private BigDecimal commissionAmount; // 提成金额
    private BigDecimal actualAmount;   // 商家实际到账金额
    private String orderNo;            // 订单编号
    private String orderTitle;         // 订单标题
    private String remark;             // 备注
    private Integer status;            // 状态: 0待结算 1已结算
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String merchantName;       // 商户名称
    @TableField(exist = false)
    private String shopName;           // 店铺名称

    // ============ Getters and Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Long getMerchantUserId() { return merchantUserId; }
    public void setMerchantUserId(Long merchantUserId) { this.merchantUserId = merchantUserId; }

    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }

    public Integer getStageType() { return stageType; }
    public void setStageType(Integer stageType) { this.stageType = stageType; }

    public BigDecimal getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public BigDecimal getActualAmount() { return actualAmount; }
    public void setActualAmount(BigDecimal actualAmount) { this.actualAmount = actualAmount; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public String getOrderTitle() { return orderTitle; }
    public void setOrderTitle(String orderTitle) { this.orderTitle = orderTitle; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
}
