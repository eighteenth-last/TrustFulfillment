package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单阶段实体
 * 
 * 阶段类型(stageType): 1首付款, 2尾款, 3质保款
 * 阶段状态(status): 0未开始, 1已托管, 2待验收, 3已验收/已结算, 4质保中, 5质保已释放
 */
@TableName("tf_order_stage")
public class TfOrderStage {

    // 阶段类型常量
    public static final int TYPE_DEPOSIT = 1;   // 首付款
    public static final int TYPE_FINAL = 2;     // 尾款
    public static final int TYPE_WARRANTY = 3;  // 质保款

    // 阶段状态常量
    public static final int STATUS_PENDING = 0;     // 未开始
    public static final int STATUS_ESCROWED = 1;    // 已托管
    public static final int STATUS_DELIVERY = 2;    // 待验收
    public static final int STATUS_COMPLETED = 3;   // 已验收/已结算
    public static final int STATUS_WARRANTY = 4;    // 质保中
    public static final int STATUS_WARRANTY_RELEASED = 5; // 质保已释放

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String stageName;
    private BigDecimal amount;
    private Integer percent;        // 付款比例百分比
    private String milestone;       // 付款节点
    private String riskNote;        // 风险止损说明
    private Integer status;
    private Integer stageType;      // 阶段类型: 1首付款, 2尾款, 3质保款
    private Integer sort;
    private String evidenceUrl;
    private String evidenceHash;
    private String deliveryDesc;
    private LocalDateTime deliveryTime;
    private LocalDateTime acceptTime;
    private LocalDateTime releaseTime;      // 款项释放时间
    private LocalDateTime warrantyEndTime;  // 质保期结束时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public String getRiskNote() {
        return riskNote;
    }

    public void setRiskNote(String riskNote) {
        this.riskNote = riskNote;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getEvidenceUrl() {
        return evidenceUrl;
    }

    public void setEvidenceUrl(String evidenceUrl) {
        this.evidenceUrl = evidenceUrl;
    }

    public String getEvidenceHash() {
        return evidenceHash;
    }

    public void setEvidenceHash(String evidenceHash) {
        this.evidenceHash = evidenceHash;
    }

    public String getDeliveryDesc() {
        return deliveryDesc;
    }

    public void setDeliveryDesc(String deliveryDesc) {
        this.deliveryDesc = deliveryDesc;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public LocalDateTime getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(LocalDateTime acceptTime) {
        this.acceptTime = acceptTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getStageType() {
        return stageType;
    }

    public void setStageType(Integer stageType) {
        this.stageType = stageType;
    }

    public LocalDateTime getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(LocalDateTime releaseTime) {
        this.releaseTime = releaseTime;
    }

    public LocalDateTime getWarrantyEndTime() {
        return warrantyEndTime;
    }

    public void setWarrantyEndTime(LocalDateTime warrantyEndTime) {
        this.warrantyEndTime = warrantyEndTime;
    }
}
