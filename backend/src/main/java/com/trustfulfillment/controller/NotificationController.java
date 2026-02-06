package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.SysNotification;
import com.trustfulfillment.entity.TfServiceMessage;
import com.trustfulfillment.entity.TfOrderMessage;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.mapper.TfServiceMessageMapper;
import com.trustfulfillment.mapper.TfOrderMessageMapper;
import com.trustfulfillment.mapper.TfOrderMapper;
import com.trustfulfillment.service.NotificationService;
import com.trustfulfillment.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private TfServiceMessageMapper serviceMessageMapper;
    
    @Autowired
    private TfOrderMessageMapper orderMessageMapper;
    
    @Autowired
    private TfOrderMapper orderMapper;
    
    @Autowired
    private SysUserService userService;

    /**
     * 获取通知列表（包含系统通知和未读消息）
     */
    @GetMapping
    public Result<List<Map<String, Object>>> getNotifications(
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser currentUser = userService.getById(userId);
        boolean isMerchant = currentUser != null && currentUser.getIsMerchant() != null && currentUser.getIsMerchant() == 1;
        
        List<Map<String, Object>> allNotifications = new ArrayList<>();
        
        // 1. 获取系统通知
        List<SysNotification> sysNotifications = notificationService.getNotifications(userId, limit);
        for (SysNotification noti : sysNotifications) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", "sys_" + noti.getId());
            item.put("type", noti.getType() != null ? noti.getType() : "system");
            item.put("title", noti.getTitle());
            item.put("content", noti.getContent());
            item.put("isRead", noti.getIsRead() == 1);
            item.put("createTime", noti.getCreateTime());
            item.put("targetType", noti.getTargetType());
            item.put("targetId", noti.getTargetId());
            item.put("notificationType", "system");
            allNotifications.add(item);
        }
        
        // 2. 获取未读客服消息（管理员发送的）
        LambdaQueryWrapper<TfServiceMessage> serviceWrapper = new LambdaQueryWrapper<>();
        serviceWrapper.eq(TfServiceMessage::getUserId, userId)
                      .eq(TfServiceMessage::getSenderRole, 2) // 管理员发送的
                      .eq(TfServiceMessage::getIsRead, 0)
                      .orderByDesc(TfServiceMessage::getCreateTime)
                      .last("LIMIT 10");
        List<TfServiceMessage> serviceMessages = serviceMessageMapper.selectList(serviceWrapper);
        
        for (TfServiceMessage msg : serviceMessages) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", "service_" + msg.getId());
            item.put("type", "service_message");
            item.put("title", "客服消息");
            item.put("content", truncateContent(msg.getContent()));
            item.put("isRead", false);
            item.put("createTime", msg.getCreateTime());
            item.put("targetType", "service_chat");
            item.put("targetId", null);
            item.put("notificationType", "service_message");
            allNotifications.add(item);
        }
        
        // 3. 获取未读订单聊天消息（接收者是当前用户且未读的消息）
        LambdaQueryWrapper<TfOrderMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(TfOrderMessage::getReceiverId, userId)
                  .eq(TfOrderMessage::getIsRead, 0)
                  .orderByDesc(TfOrderMessage::getCreateTime)
                  .last("LIMIT 10");
        List<TfOrderMessage> orderMessages = orderMessageMapper.selectList(msgWrapper);
        
        for (TfOrderMessage msg : orderMessages) {
            // 获取订单信息
            TfOrder order = orderMapper.selectById(msg.getOrderId());
            String orderTitle = order != null ? order.getTitle() : "订单";
            
            // 获取发送者名称
            String senderName = "用户";
            SysUser sender = userService.getById(msg.getSenderId());
            if (sender != null) {
                senderName = sender.getNickname() != null ? sender.getNickname() : "用户";
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("id", "order_msg_" + msg.getId());
            item.put("type", "order_message");
            item.put("title", senderName + " - " + truncateTitle(orderTitle));
            item.put("content", truncateContent(msg.getContent()));
            item.put("isRead", false);
            item.put("createTime", msg.getCreateTime());
            item.put("targetType", "order_chat");
            item.put("targetId", msg.getOrderId());
            item.put("notificationType", "order_message");
            allNotifications.add(item);
        }
        
        // 按时间排序
        allNotifications.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("createTime");
            LocalDateTime timeB = (LocalDateTime) b.get("createTime");
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return timeB.compareTo(timeA);
        });
        
        // 限制返回数量
        if (allNotifications.size() > limit) {
            allNotifications = allNotifications.subList(0, limit);
        }
        
        return Result.success(allNotifications);
    }
    
    // 截断标题
    private String truncateTitle(String title) {
        if (title == null) return "";
        return title.length() > 15 ? title.substring(0, 15) + "..." : title;
    }
    
    // 截断内容
    private String truncateContent(String content) {
        if (content == null) return "";
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> getUnreadCount() {
        Long userId = StpUtil.getLoginIdAsLong();
        int count = notificationService.getUnreadCount(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    /**
     * 标记单条通知为已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable("id") Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = StpUtil.getLoginIdAsLong();
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable("id") Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        notificationService.deleteNotification(id, userId);
        return Result.success();
    }
}
