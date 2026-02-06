package com.trustfulfillment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.TfOrderMessage;
import com.trustfulfillment.mapper.SysUserMapper;
import com.trustfulfillment.mapper.TfOrderMessageMapper;
import com.trustfulfillment.service.NotificationService;
import com.trustfulfillment.service.OrderMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单消息服务实现
 */
@Service
public class OrderMessageServiceImpl implements OrderMessageService {

    @Autowired
    private TfOrderMessageMapper messageMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public TfOrderMessage sendMessage(Long orderId, Long senderId, Long receiverId, String content, Integer type) {
        TfOrderMessage message = new TfOrderMessage();
        message.setOrderId(orderId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setType(type != null ? type : 1);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        
        messageMapper.insert(message);
        
        // 填充发送者信息
        SysUser sender = userMapper.selectById(senderId);
        if (sender != null) {
            message.setSenderName(sender.getNickname() != null ? sender.getNickname() : sender.getPhone());
            message.setSenderAvatar(sender.getAvatar());
        }
        
        // 发送通知给接收者
        String senderName = sender != null ? 
            (sender.getNickname() != null ? sender.getNickname() : sender.getPhone()) : "用户";
        String notifyTitle = "收到新消息";
        String notifyContent = senderName + ": " + (content.length() > 50 ? content.substring(0, 50) + "..." : content);
        notificationService.sendNotification(receiverId, "message", notifyTitle, notifyContent, "order", orderId);
        
        return message;
    }

    @Override
    public List<TfOrderMessage> getMessageHistory(Long orderId, Long userId) {
        LambdaQueryWrapper<TfOrderMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfOrderMessage::getOrderId, orderId)
                .and(w -> w.eq(TfOrderMessage::getSenderId, userId)
                        .or()
                        .eq(TfOrderMessage::getReceiverId, userId))
                .orderByAsc(TfOrderMessage::getCreateTime);
        
        List<TfOrderMessage> messages = messageMapper.selectList(wrapper);
        
        // 获取所有发送者ID
        List<Long> senderIds = messages.stream()
                .map(TfOrderMessage::getSenderId)
                .distinct()
                .collect(Collectors.toList());
        
        if (!senderIds.isEmpty()) {
            // 批量查询用户信息
            List<SysUser> users = userMapper.selectByIds(senderIds);
            Map<Long, SysUser> userMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, u -> u));
            
            // 填充发送者信息
            for (TfOrderMessage msg : messages) {
                SysUser sender = userMap.get(msg.getSenderId());
                if (sender != null) {
                    msg.setSenderName(sender.getNickname() != null ? sender.getNickname() : sender.getPhone());
                    msg.setSenderAvatar(sender.getAvatar());
                }
            }
        }
        
        return messages;
    }

    @Override
    public int markAsRead(Long orderId, Long receiverId) {
        return messageMapper.markAsRead(orderId, receiverId);
    }

    @Override
    public int getUnreadCount(Long orderId, Long receiverId) {
        LambdaQueryWrapper<TfOrderMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfOrderMessage::getOrderId, orderId)
                .eq(TfOrderMessage::getReceiverId, receiverId)
                .eq(TfOrderMessage::getIsRead, 0);
        return Math.toIntExact(messageMapper.selectCount(wrapper));
    }
}
