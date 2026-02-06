package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 银行信息实体
 */
@TableName("sys_bank")
public class SysBank {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 银行代码
     */
    private String bankCode;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行Logo URL
     */
    private String logoUrl;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态: 0禁用, 1启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ============ Getters and Setters ============

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
