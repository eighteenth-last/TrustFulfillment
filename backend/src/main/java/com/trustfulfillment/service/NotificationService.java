package com.trustfulfillment.service;

import com.trustfulfillment.entity.SysNotification;
import java.util.List;

public interface NotificationService {

    /**
     * 获取用户通知列表
     */
    List<SysNotification> getNotifications(Long userId, Integer limit);

    /**
     * 获取未读通知数量
     */
    int getUnreadCount(Long userId);

    /**
     * 标记单条通知为已读
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Long userId);

    /**
     * 发送通知
     */
    void sendNotification(Long userId, String type, String title, String content, String targetType, Long targetId);

    /**
     * 删除通知
     */
    void deleteNotification(Long notificationId, Long userId);
}
