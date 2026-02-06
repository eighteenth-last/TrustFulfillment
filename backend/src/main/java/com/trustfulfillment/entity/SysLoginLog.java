package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * 用户登录日志实体
 */
@TableName("sys_login_log")
public class SysLoginLog {

    public static final String TYPE_PHONE_CODE = "phone_code";
    public static final String TYPE_PHONE_PWD = "phone_pwd";
    public static final String TYPE_WECHAT = "wechat";

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String loginType;
    private String phone;
    private String openid;
    private String ip;
    private String device;
    private String platform;
    private Integer status;
    private String failReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ============ Getters and Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getLoginType() { return loginType; }
    public void setLoginType(String loginType) { this.loginType = loginType; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
