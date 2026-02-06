package com.trustfulfillment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trustfulfillment.entity.SysNotification;
import com.trustfulfillment.mapper.SysNotificationMapper;
import com.trustfulfillment.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private SysNotificationMapper notificationMapper;

    @Override
    public List<SysNotification> getNotifications(Long userId, Integer limit) {
        LambdaQueryWrapper<SysNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotification::getUserId, userId)
               .orderByDesc(SysNotification::getCreateTime);
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        return notificationMapper.selectList(wrapper);
    }

    @Override
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<SysNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotification::getUserId, userId)
               .eq(SysNotification::getIsRead, 0);
        return Math.toIntExact(notificationMapper.selectCount(wrapper));
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        SysNotification notification = notificationMapper.selectById(notificationId);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(1);
            notification.setReadTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    public void sendNotification(Long userId, String type, String title, String content, String targetType, Long targetId) {
        SysNotification notification = new SysNotification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setTargetType(targetType);
        notification.setTargetId(targetId);
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        notificationMapper.insert(notification);
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        LambdaQueryWrapper<SysNotification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotification::getId, notificationId)
               .eq(SysNotification::getUserId, userId);
        notificationMapper.delete(wrapper);
    }
}
