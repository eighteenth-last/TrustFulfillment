package com.trustfulfillment.websocket;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.entity.TfOrderMessage;
import com.trustfulfillment.mapper.TfOrderMapper;
import com.trustfulfillment.service.OrderMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 聊天处理器
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    // 存储用户连接: userId -> session
    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    
    // Redis 消息通道前缀
    private static final String CHAT_CHANNEL_PREFIX = "chat:order:";

    @Autowired
    private OrderMessageService messageService;

    @Autowired
    private TfOrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisMessageListenerContainer listenerContainer;

    @PostConstruct
    public void init() {
        log.info("ChatWebSocketHandler 初始化完成");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从URL参数获取token
        String query = session.getUri().getQuery();
        String token = null;
        if (query != null) {
            for (String param : query.split("&")) {
                String[] kv = param.split("=");
                if (kv.length == 2 && "token".equals(kv[0])) {
                    token = kv[1];
                    break;
                }
            }
        }

        if (token == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("缺少token参数"));
            return;
        }

        // 验证token并获取用户ID
        Object loginId;
        try {
            loginId = StpUtil.getLoginIdByToken(token);
        } catch (Exception e) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("无效的token"));
            return;
        }

        if (loginId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("token已过期"));
            return;
        }

        Long userId = Long.parseLong(loginId.toString());
        
        // 存储会话
        userSessions.put(userId, session);
        session.getAttributes().put("userId", userId);
        
        log.info("用户 {} 连接WebSocket成功", userId);
        
        // 发送连接成功消息
        sendToSession(session, createSystemMessage("connected", "连接成功"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            return;
        }

        try {
            JSONObject json = JSONUtil.parseObj(message.getPayload());
            String action = json.getStr("action");

            switch (action) {
                case "send" -> handleSendMessage(session, userId, json);
                case "read" -> handleMarkRead(userId, json);
                case "subscribe" -> handleSubscribe(session, userId, json);
                default -> log.warn("未知的action: {}", action);
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
            sendToSession(session, createErrorMessage("消息处理失败: " + e.getMessage()));
        }
    }

    /**
     * 处理发送消息
     */
    private void handleSendMessage(WebSocketSession session, Long senderId, JSONObject json) {
        Long orderId = json.getLong("orderId");
        String content = json.getStr("content");
        Integer type = json.getInt("type", 1);

        if (orderId == null || content == null || content.trim().isEmpty()) {
            sendToSession(session, createErrorMessage("参数不完整"));
            return;
        }

        // 获取订单信息，确定接收者
        TfOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            sendToSession(session, createErrorMessage("订单不存在"));
            return;
        }

        // 确定接收者ID（发送者是用户则接收者是商家，反之亦然）
        Long receiverId;
        if (senderId.equals(order.getBuyerId())) {
            receiverId = order.getSellerId();
        } else if (senderId.equals(order.getSellerId())) {
            receiverId = order.getBuyerId();
        } else {
            sendToSession(session, createErrorMessage("您无权在此订单中发送消息"));
            return;
        }

        // 保存消息到数据库
        TfOrderMessage msg = messageService.sendMessage(orderId, senderId, receiverId, content, type);

        // 构建消息响应
        JSONObject msgJson = new JSONObject();
        msgJson.set("type", "message");
        msgJson.set("data", msg);

        // 发送给发送者（确认消息已发送）
        sendToSession(session, msgJson.toString());

        // 通过Redis发布消息，实现跨实例推送
        String channel = CHAT_CHANNEL_PREFIX + orderId;
        redisTemplate.convertAndSend(channel, msgJson.toString());

        // 直接推送给接收者（同实例）
        WebSocketSession receiverSession = userSessions.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            sendToSession(receiverSession, msgJson.toString());
        }
    }

    /**
     * 处理标记已读
     */
    private void handleMarkRead(Long userId, JSONObject json) {
        Long orderId = json.getLong("orderId");
        if (orderId != null) {
            messageService.markAsRead(orderId, userId);
        }
    }

    /**
     * 处理订阅订单消息
     */
    private void handleSubscribe(WebSocketSession session, Long userId, JSONObject json) {
        Long orderId = json.getLong("orderId");
        if (orderId == null) {
            sendToSession(session, createErrorMessage("缺少orderId参数"));
            return;
        }

        // 验证用户是否有权限访问该订单
        TfOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            sendToSession(session, createErrorMessage("订单不存在"));
            return;
        }

        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            sendToSession(session, createErrorMessage("您无权访问此订单"));
            return;
        }

        // 存储订阅的订单ID
        session.getAttributes().put("subscribedOrderId", orderId);

        // 订阅Redis通道
        String channel = CHAT_CHANNEL_PREFIX + orderId;
        listenerContainer.addMessageListener((message, pattern) -> {
            if (session.isOpen()) {
                Long subscribedOrderId = (Long) session.getAttributes().get("subscribedOrderId");
                if (orderId.equals(subscribedOrderId)) {
                    String msgContent = new String(message.getBody());
                    sendToSession(session, msgContent);
                }
            }
        }, new ChannelTopic(channel));

        // 标记已读
        messageService.markAsRead(orderId, userId);

        // 发送订阅成功消息
        sendToSession(session, createSystemMessage("subscribed", "已订阅订单 " + orderId + " 的消息"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.remove(userId);
            log.info("用户 {} 断开WebSocket连接: {}", userId, status);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        log.error("用户 {} WebSocket传输错误", userId, exception);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    /**
     * 发送消息到指定会话
     */
    private void sendToSession(WebSocketSession session, String message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("发送WebSocket消息失败", e);
            }
        }
    }

    /**
     * 创建系统消息
     */
    private String createSystemMessage(String action, String message) {
        JSONObject json = new JSONObject();
        json.set("type", "system");
        json.set("action", action);
        json.set("message", message);
        return json.toString();
    }

    /**
     * 创建错误消息
     */
    private String createErrorMessage(String message) {
        JSONObject json = new JSONObject();
        json.set("type", "error");
        json.set("message", message);
        return json.toString();
    }
}
