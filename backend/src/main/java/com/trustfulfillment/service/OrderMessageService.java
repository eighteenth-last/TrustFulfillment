package com.trustfulfillment.service;

import com.trustfulfillment.entity.TfOrderMessage;
import java.util.List;

/**
 * 订单消息服务接口
 */
public interface OrderMessageService {

    /**
     * 发送消息
     */
    TfOrderMessage sendMessage(Long orderId, Long senderId, Long receiverId, String content, Integer type);

    /**
     * 获取订单的消息历史
     */
    List<TfOrderMessage> getMessageHistory(Long orderId, Long userId);

    /**
     * 标记消息为已读
     */
    int markAsRead(Long orderId, Long receiverId);

    /**
     * 获取未读消息数量
     */
    int getUnreadCount(Long orderId, Long receiverId);
}
