package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 订单消息实体
 */
@TableName("tf_order_message")
public class TfOrderMessage {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;           // 订单ID
    private Long senderId;          // 发送者ID
    private Long receiverId;        // 接收者ID
    private String content;         // 消息内容
    private Integer type;           // 类型: 1文本, 2图片, 3文件
    private Integer isRead;         // 是否已读: 0否, 1是
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 非数据库字段
    @TableField(exist = false)
    private String senderName;      // 发送者名称
    @TableField(exist = false)
    private String senderAvatar;    // 发送者头像

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }
}
