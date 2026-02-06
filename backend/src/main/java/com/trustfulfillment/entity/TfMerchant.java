package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户实体
 */
@TableName("tf_merchant")
public class TfMerchant {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;              // 关联用户ID
    private String merchantNo;        // 商户编号
    private Integer merchantType;     // 商户类型: 1个体工商户 2企业/组织
    private String shopName;          // 店铺名称
    private String companyName;       // 企业/组织名称
    private String legalPerson;       // 法人/负责人姓名
    private String legalIdCard;       // 法人身份证号
    private String licenseNo;         // 营业执照号
    private String licenseImg;        // 营业执照图片URL
    private String idCardFront;       // 身份证正面图片URL
    private String idCardBack;        // 身份证反面图片URL
    private String otherDocs;         // 其他资质文档URL（JSON数组）
    private String contactPhone;      // 联系电话
    private String contactEmail;      // 联系邮箱
    private String businessAddress;   // 经营地址
    private String businessScope;     // 经营范围
    private String businessCategories;// 业务分类
    private BigDecimal commissionRate;// 平台提成比例
    private Integer creditScore;      // 信用评分
    private Integer orderCount;       // 完成订单数
    private BigDecimal totalIncome;   // 累计收入
    private Integer status;           // 状态: 0待审核 1正常 2禁用
    private LocalDateTime verifyTime; // 认证时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String userName;  // 关联用户昵称

    // ============ Getters and Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMerchantNo() { return merchantNo; }
    public void setMerchantNo(String merchantNo) { this.merchantNo = merchantNo; }

    public Integer getMerchantType() { return merchantType; }
    public void setMerchantType(Integer merchantType) { this.merchantType = merchantType; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLegalPerson() { return legalPerson; }
    public void setLegalPerson(String legalPerson) { this.legalPerson = legalPerson; }

    public String getLegalIdCard() { return legalIdCard; }
    public void setLegalIdCard(String legalIdCard) { this.legalIdCard = legalIdCard; }

    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

    public String getLicenseImg() { return licenseImg; }
    public void setLicenseImg(String licenseImg) { this.licenseImg = licenseImg; }

    public String getIdCardFront() { return idCardFront; }
    public void setIdCardFront(String idCardFront) { this.idCardFront = idCardFront; }

    public String getIdCardBack() { return idCardBack; }
    public void setIdCardBack(String idCardBack) { this.idCardBack = idCardBack; }

    public String getOtherDocs() { return otherDocs; }
    public void setOtherDocs(String otherDocs) { this.otherDocs = otherDocs; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }

    public String getBusinessScope() { return businessScope; }
    public void setBusinessScope(String businessScope) { this.businessScope = businessScope; }

    public String getBusinessCategories() { return businessCategories; }
    public void setBusinessCategories(String businessCategories) { this.businessCategories = businessCategories; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }

    public Integer getOrderCount() { return orderCount; }
    public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getVerifyTime() { return verifyTime; }
    public void setVerifyTime(LocalDateTime verifyTime) { this.verifyTime = verifyTime; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
