package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 纠纷实体
 */
@TableName("tf_dispute")
public class TfDispute {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long initiatorId;
    private String initiatorRole;  // user/merchant
    private String reason;
    private String evidenceUrls;   // JSON数组
    private Integer status;        // 0待处理, 1处理中, 2已裁决, 3已撤销
    private Integer result;        // 1甲方胜, 2乙方胜, 3各打五十大板
    private String resultDesc;
    private Long adminId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private LocalDateTime resolvedTime;

    // 非数据库字段
    @TableField(exist = false)
    private String orderTitle;
    @TableField(exist = false)
    private String initiatorName;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Long getInitiatorId() { return initiatorId; }
    public void setInitiatorId(Long initiatorId) { this.initiatorId = initiatorId; }
    
    public String getInitiatorRole() { return initiatorRole; }
    public void setInitiatorRole(String initiatorRole) { this.initiatorRole = initiatorRole; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getEvidenceUrls() { return evidenceUrls; }
    public void setEvidenceUrls(String evidenceUrls) { this.evidenceUrls = evidenceUrls; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public Integer getResult() { return result; }
    public void setResult(Integer result) { this.result = result; }
    
    public String getResultDesc() { return resultDesc; }
    public void setResultDesc(String resultDesc) { this.resultDesc = resultDesc; }
    
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    public LocalDateTime getResolvedTime() { return resolvedTime; }
    public void setResolvedTime(LocalDateTime resolvedTime) { this.resolvedTime = resolvedTime; }
    
    public String getOrderTitle() { return orderTitle; }
    public void setOrderTitle(String orderTitle) { this.orderTitle = orderTitle; }
    
    public String getInitiatorName() { return initiatorName; }
    public void setInitiatorName(String initiatorName) { this.initiatorName = initiatorName; }
}
