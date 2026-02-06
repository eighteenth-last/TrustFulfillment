package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 风险事件记录实体
 * 记录系统检测到的各类风险事件
 */
@TableName("tf_risk_event")
public class TfRiskEvent {

    // 风险等级
    public static final String LEVEL_HIGH = "high";
    public static final String LEVEL_MEDIUM = "medium";
    public static final String LEVEL_LOW = "low";
    
    // 风险类型
    public static final String TYPE_LARGE_TRANSACTION = "large_transaction";      // 大额交易
    public static final String TYPE_FREQUENT_OPERATION = "frequent_operation";    // 频繁操作
    public static final String TYPE_ABNORMAL_LOGIN = "abnormal_login";            // 异常登录
    public static final String TYPE_DISPUTE = "dispute";                          // 纠纷风险
    public static final String TYPE_ACCOUNT_ANOMALY = "account_anomaly";          // 账户异常
    public static final String TYPE_IP_RISK = "ip_risk";                          // IP风险
    public static final String TYPE_FRAUD_SUSPICION = "fraud_suspicion";          // 欺诈嫌疑
    
    // 状态
    public static final int STATUS_PENDING = 0;      // 待处理
    public static final int STATUS_PROCESSED = 1;    // 已处理
    public static final int STATUS_IGNORED = 2;      // 已忽略
    public static final int STATUS_BLOCKED = 3;      // 已拦截

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String eventType;          // 风险类型
    private String riskLevel;          // 风险等级: high/medium/low
    private Long userId;               // 关联用户ID
    private Long orderId;              // 关联订单ID（可为空）
    private Long disputeId;            // 关联纠纷ID（可为空）
    private String title;              // 事件标题
    private String description;        // 事件描述
    private BigDecimal amount;         // 涉及金额（可为空）
    private String ipAddress;          // IP地址（可为空）
    private String userAgent;          // 用户代理（可为空）
    private String extraData;          // 额外数据（JSON格式）
    private Integer status;            // 状态: 0待处理 1已处理 2已忽略 3已拦截
    private Long processedBy;          // 处理人ID
    private String processRemark;      // 处理备注
    private LocalDateTime processTime; // 处理时间
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String userName;           // 用户名称
    @TableField(exist = false)
    private String orderNo;            // 订单编号
    @TableField(exist = false)
    private String processedByName;    // 处理人名称

    // ============ Getters and Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getDisputeId() { return disputeId; }
    public void setDisputeId(Long disputeId) { this.disputeId = disputeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getProcessedBy() { return processedBy; }
    public void setProcessedBy(Long processedBy) { this.processedBy = processedBy; }

    public String getProcessRemark() { return processRemark; }
    public void setProcessRemark(String processRemark) { this.processRemark = processRemark; }

    public LocalDateTime getProcessTime() { return processTime; }
    public void setProcessTime(LocalDateTime processTime) { this.processTime = processTime; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public String getProcessedByName() { return processedByName; }
    public void setProcessedByName(String processedByName) { this.processedByName = processedByName; }
}
