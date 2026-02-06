package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * 客服消息实体
 * 用于用户与平台客服（管理员）之间的聊天
 */
@TableName("tf_service_message")
public class TfServiceMessage {

    public static final int TYPE_TEXT = 1;    // 文本
    public static final int TYPE_IMAGE = 2;   // 图片
    public static final int TYPE_FILE = 3;    // 文件

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;           // 用户ID（发起咨询的用户）
    private Long senderId;         // 发送者ID
    private Integer senderRole;    // 发送者角色: 1用户 2管理员
    private String content;        // 消息内容
    private Integer type;          // 消息类型: 1文本 2图片 3文件
    private Integer isRead;        // 是否已读: 0未读 1已读

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 非数据库字段
    @TableField(exist = false)
    private String senderName;     // 发送者名称
    @TableField(exist = false)
    private String senderAvatar;   // 发送者头像

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Integer getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(Integer senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }
}
