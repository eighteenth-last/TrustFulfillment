package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录实体
 */
@TableName("tf_transaction")
public class TfTransaction {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long orderId;
    private Long stageId;
    private String type;           // recharge充值, withdraw提现, deposit托管, release释放, refund退款
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String title;
    private String remark;
    private Integer status;        // 0处理中, 1成功, 2失败
    private String evidenceHash;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public String getEvidenceHash() { return evidenceHash; }
    public void setEvidenceHash(String evidenceHash) { this.evidenceHash = evidenceHash; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
