package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 纠纷留言实体
 */
@TableName("tf_dispute_message")
public class TfDisputeMessage {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long disputeId;
    private Long userId;
    private String role;           // user/merchant/admin
    private String content;
    private String attachments;    // JSON数组
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 非数据库字段
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userAvatar;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getDisputeId() { return disputeId; }
    public void setDisputeId(Long disputeId) { this.disputeId = disputeId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getAttachments() { return attachments; }
    public void setAttachments(String attachments) { this.attachments = attachments; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
}
