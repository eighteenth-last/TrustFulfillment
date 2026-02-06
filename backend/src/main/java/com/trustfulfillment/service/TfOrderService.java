package com.trustfulfillment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.entity.TfOrderStage;

import java.util.List;
import java.util.Map;

public interface TfOrderService extends IService<TfOrder> {

    /**
     * 创建订单
     */
    TfOrder createOrder(TfOrder order, List<TfOrderStage> stages);

    /**
     * 分页查询订单
     */
    IPage<TfOrder> pageOrders(Long userId, String role, Integer status, int page, int size);

    /**
     * 获取订单详情（包含阶段信息）
     */
    Map<String, Object> getOrderDetail(Long orderId);

    /**
     * 接单
     */
    boolean acceptOrder(Long orderId, Long sellerId);

    /**
     * 托管资金（用户付款第一阶段，订单正式发布）
     */
    boolean depositFunds(Long orderId, Long buyerId);

    /**
     * 支付下一阶段款项
     */
    boolean payNextStage(Long orderId, Long buyerId);

    /**
     * 开始阶段（商家开始执行某阶段）
     */
    boolean startStage(Long orderId, Long stageId, Long sellerId);

    /**
     * 提交交付（商家提交阶段交付物）
     */
    boolean submitDelivery(Long orderId, Long stageId, String evidenceUrl, String deliveryDesc, Long sellerId);

    /**
     * 验收确认（用户验收通过）
     */
    boolean acceptDelivery(Long orderId, Long stageId, Long buyerId);

    /**
     * 拒绝验收（用户验收不通过）
     */
    boolean rejectDelivery(Long orderId, Long stageId, Long buyerId, String reason);

    /**
     * 获取阶段详情
     */
    TfOrderStage getStageDetail(Long stageId);

    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId, Long userId);

    /**
     * 释放质保款
     */
    boolean releaseWarranty(Long orderId, Long buyerId);

    /**
     * 获取用户统计数据
     */
    Map<String, Object> getUserStats(Long userId, String role);
}
