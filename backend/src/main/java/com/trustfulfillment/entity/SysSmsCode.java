package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * 短信验证码实体
 */
@TableName("sys_sms_code")
public class SysSmsCode {

    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_BIND_PHONE = 2;
    public static final int TYPE_RESET_PASSWORD = 3;
    public static final int TYPE_PAYMENT = 4;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String code;
    private Integer type;
    private String ip;
    private Integer used;
    private LocalDateTime expireTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ============ Getters and Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public Integer getUsed() { return used; }
    public void setUsed(Integer used) { this.used = used; }

    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
