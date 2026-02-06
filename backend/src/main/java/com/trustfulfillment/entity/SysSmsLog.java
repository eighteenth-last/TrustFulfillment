package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * 短信发送记录实体
 */
@TableName("sys_sms_log")
public class SysSmsLog {

    public static final int STATUS_SENDING = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 2;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String phone;
    private String templateCode;
    private String content;
    private Integer type;
    private String provider;
    private String requestId;
    private String bizId;
    private Integer status;
    private String errorCode;
    private String errorMsg;
    private String ip;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // ============ Getters and Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getBizId() { return bizId; }
    public void setBizId(String bizId) { this.bizId = bizId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
