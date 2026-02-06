package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目合同实体
 * 状态: 0草稿 1待甲方签署 2待乙方签署 3已生效 4已作废
 */
@TableName("tf_contract")
public class TfContract {

    public static final int STATUS_DRAFT = 0;           // 草稿
    public static final int STATUS_WAIT_PARTY_A = 1;    // 待甲方签署
    public static final int STATUS_WAIT_PARTY_B = 2;    // 待乙方签署(商家先签后到这个状态)
    public static final int STATUS_EFFECTIVE = 3;       // 已生效
    public static final int STATUS_INVALID = 4;         // 已作废

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;           // 关联订单ID
    private String contractNo;      // 合同编号
    private String title;           // 合同标题
    
    // 合同双方
    private Long partyAId;          // 甲方ID(用户)
    private String partyAName;      // 甲方名称
    private Long partyBId;          // 乙方ID(商家)
    private String partyBName;      // 乙方名称
    
    // 项目信息
    private String projectDesc;     // 项目描述
    private String techRequirements; // 技术要求
    private String deliveryStandard; // 验收标准
    private LocalDate deliveryDeadline; // 交付期限
    
    // 付款条款
    private BigDecimal totalAmount; // 合同总金额
    private String paymentTerms;    // 付款条款JSON
    
    // 法律条款
    private String breachClause;        // 违约条款
    private String confidentialClause;  // 保密协议
    private String ipClause;            // 知识产权归属
    private String disputeClause;       // 争议解决方式
    
    // 签署信息
    private String partyASignature;     // 甲方签名(Base64)
    private LocalDateTime partyASignTime; // 甲方签署时间
    private String partyBSignature;     // 乙方签名(Base64)
    private LocalDateTime partyBSignTime; // 乙方签署时间
    
    private Integer status;         // 状态
    private String evidenceHash;    // 存证哈希

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

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

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPartyAId() {
        return partyAId;
    }

    public void setPartyAId(Long partyAId) {
        this.partyAId = partyAId;
    }

    public String getPartyAName() {
        return partyAName;
    }

    public void setPartyAName(String partyAName) {
        this.partyAName = partyAName;
    }

    public Long getPartyBId() {
        return partyBId;
    }

    public void setPartyBId(Long partyBId) {
        this.partyBId = partyBId;
    }

    public String getPartyBName() {
        return partyBName;
    }

    public void setPartyBName(String partyBName) {
        this.partyBName = partyBName;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public String getTechRequirements() {
        return techRequirements;
    }

    public void setTechRequirements(String techRequirements) {
        this.techRequirements = techRequirements;
    }

    public String getDeliveryStandard() {
        return deliveryStandard;
    }

    public void setDeliveryStandard(String deliveryStandard) {
        this.deliveryStandard = deliveryStandard;
    }

    public LocalDate getDeliveryDeadline() {
        return deliveryDeadline;
    }

    public void setDeliveryDeadline(LocalDate deliveryDeadline) {
        this.deliveryDeadline = deliveryDeadline;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getBreachClause() {
        return breachClause;
    }

    public void setBreachClause(String breachClause) {
        this.breachClause = breachClause;
    }

    public String getConfidentialClause() {
        return confidentialClause;
    }

    public void setConfidentialClause(String confidentialClause) {
        this.confidentialClause = confidentialClause;
    }

    public String getIpClause() {
        return ipClause;
    }

    public void setIpClause(String ipClause) {
        this.ipClause = ipClause;
    }

    public String getDisputeClause() {
        return disputeClause;
    }

    public void setDisputeClause(String disputeClause) {
        this.disputeClause = disputeClause;
    }

    public String getPartyASignature() {
        return partyASignature;
    }

    public void setPartyASignature(String partyASignature) {
        this.partyASignature = partyASignature;
    }

    public LocalDateTime getPartyASignTime() {
        return partyASignTime;
    }

    public void setPartyASignTime(LocalDateTime partyASignTime) {
        this.partyASignTime = partyASignTime;
    }

    public String getPartyBSignature() {
        return partyBSignature;
    }

    public void setPartyBSignature(String partyBSignature) {
        this.partyBSignature = partyBSignature;
    }

    public LocalDateTime getPartyBSignTime() {
        return partyBSignTime;
    }

    public void setPartyBSignTime(LocalDateTime partyBSignTime) {
        this.partyBSignTime = partyBSignTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEvidenceHash() {
        return evidenceHash;
    }

    public void setEvidenceHash(String evidenceHash) {
        this.evidenceHash = evidenceHash;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    // ========== 非数据库字段，用于返回数据 ==========
    
    @TableField(exist = false)
    private String buyerName;           // 甲方名称
    
    @TableField(exist = false)
    private String sellerName;          // 乙方名称
    
    @TableField(exist = false)
    private List<TfOrderStage> stages;  // 付款阶段列表
    
    @TableField(exist = false)
    private BigDecimal settledAmount;   // 已结算金额
    
    @TableField(exist = false)
    private String terms;               // 合同条款（组合）

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TfOrderStage> getStages() {
        return stages;
    }

    public void setStages(List<TfOrderStage> stages) {
        this.stages = stages;
    }

    public BigDecimal getSettledAmount() {
        return settledAmount;
    }

    public void setSettledAmount(BigDecimal settledAmount) {
        this.settledAmount = settledAmount;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }
}
