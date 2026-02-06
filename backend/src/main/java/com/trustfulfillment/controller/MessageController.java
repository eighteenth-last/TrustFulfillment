package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.entity.TfOrderMessage;
import com.trustfulfillment.mapper.TfOrderMapper;
import com.trustfulfillment.service.OrderMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private OrderMessageService messageService;

    @Autowired
    private TfOrderMapper orderMapper;

    /**
     * 获取订单消息历史
     */
    @GetMapping("/history/{orderId}")
    public Result<Map<String, Object>> getMessageHistory(@PathVariable("orderId") Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证用户权限
        TfOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            return Result.error("您无权访问此订单的消息");
        }
        
        // 获取消息历史
        List<TfOrderMessage> messages = messageService.getMessageHistory(orderId, userId);
        
        // 获取未读数量
        int unreadCount = messageService.getUnreadCount(orderId, userId);
        
        // 确定对方信息
        Long otherUserId = userId.equals(order.getBuyerId()) ? order.getSellerId() : order.getBuyerId();
        
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messages);
        result.put("unreadCount", unreadCount);
        result.put("otherUserId", otherUserId);
        result.put("currentUserId", userId);
        
        return Result.success(result);
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/read/{orderId}")
    public Result<Integer> markAsRead(@PathVariable("orderId") Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证用户权限
        TfOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            return Result.error("您无权访问此订单的消息");
        }
        
        int count = messageService.markAsRead(orderId, userId);
        return Result.success(count);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/{orderId}")
    public Result<Integer> getUnreadCount(@PathVariable("orderId") Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证用户权限
        TfOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            return Result.error("您无权访问此订单的消息");
        }
        
        int count = messageService.getUnreadCount(orderId, userId);
        return Result.success(count);
    }

    /**
     * 发送消息 (HTTP方式，备用)
     */
    @PostMapping("/send")
    public Result<TfOrderMessage> sendMessage(@RequestBody Map<String, Object> params) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        Long orderId = Long.parseLong(params.get("orderId").toString());
        String content = (String) params.get("content");
        Integer type = params.get("type") != null ? Integer.parseInt(params.get("type").toString()) : 1;
        
        // 验证用户权限并获取接收者
        TfOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        Long receiverId;
        if (userId.equals(order.getBuyerId())) {
            receiverId = order.getSellerId();
        } else if (userId.equals(order.getSellerId())) {
            receiverId = order.getBuyerId();
        } else {
            return Result.error("您无权在此订单中发送消息");
        }
        
        TfOrderMessage message = messageService.sendMessage(orderId, userId, receiverId, content, type);
        return Result.success(message);
    }
}
