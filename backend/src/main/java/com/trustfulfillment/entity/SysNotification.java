package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 系统通知实体
 */
@TableName("sys_notification")
public class SysNotification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收用户ID
     */
    private Long userId;

    /**
     * 通知类型: message-消息, order-订单, dispute-纠纷, system-系统
     */
    private String type;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 关联目标类型: order, dispute, message等
     */
    private String targetType;

    /**
     * 关联目标ID
     */
    private Long targetId;

    /**
     * 是否已读: 0-未读, 1-已读
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

    /**
     * 用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String userName;

    // ============ Getters and Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getReadTime() { return readTime; }
    public void setReadTime(LocalDateTime readTime) { this.readTime = readTime; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
