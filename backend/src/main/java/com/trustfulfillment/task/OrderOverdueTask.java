package com.trustfulfillment.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.entity.TfOrderStage;
import com.trustfulfillment.mapper.TfOrderMapper;
import com.trustfulfillment.mapper.TfOrderStageMapper;
import com.trustfulfillment.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单逾期检查定时任务
 * 检查订单交付时间是否逾期，并向双方发送通知
 */
@Component
public class OrderOverdueTask {

    private static final Logger log = LoggerFactory.getLogger(OrderOverdueTask.class);

    // Redis key前缀，用于记录已发送的逾期通知，避免重复发送
    private static final String OVERDUE_NOTIFY_KEY_PREFIX = "order:overdue:notified:";

    @Autowired
    private TfOrderMapper orderMapper;

    @Autowired
    private TfOrderStageMapper stageMapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 每小时检查一次逾期订单
     * 可根据业务需求调整执行频率
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkOverdueOrders() {
        log.info("开始检查逾期订单...");
        
        try {
            // 查询进行中的订单（状态2=进行中, 3=待验收）
            LambdaQueryWrapper<TfOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(TfOrder::getStatus, 2, 3)  // 进行中或待验收
                   .isNotNull(TfOrder::getDeliveryTime)
                   .lt(TfOrder::getDeliveryTime, LocalDateTime.now());  // 交付时间已过
            
            List<TfOrder> overdueOrders = orderMapper.selectList(wrapper);
            
            log.info("发现 {} 个逾期订单", overdueOrders.size());
            
            for (TfOrder order : overdueOrders) {
                processOverdueOrder(order);
            }
            
            log.info("逾期订单检查完成");
        } catch (Exception e) {
            log.error("检查逾期订单时发生错误", e);
        }
    }

    /**
     * 处理单个逾期订单
     */
    private void processOverdueOrder(TfOrder order) {
        // 检查订单是否存在质保款阶段，存在则跳过不发送逾期提醒
        // 因为有质保款的订单通常是长期项目，有质保期保障
        LambdaQueryWrapper<TfOrderStage> stageWrapper = new LambdaQueryWrapper<>();
        stageWrapper.eq(TfOrderStage::getOrderId, order.getId())
                    .eq(TfOrderStage::getStageType, TfOrderStage.TYPE_WARRANTY);
        Long warrantyCount = stageMapper.selectCount(stageWrapper);
        if (warrantyCount != null && warrantyCount > 0) {
            log.debug("订单 {} 存在质保款阶段，跳过逾期提醒", order.getId());
            return;
        }
        
        // 计算逾期天数
        long overdueDays = ChronoUnit.DAYS.between(order.getDeliveryTime(), LocalDateTime.now());
        
        // 根据逾期天数确定通知级别（每天只通知一次）
        String notifyKey = OVERDUE_NOTIFY_KEY_PREFIX + order.getId() + ":" + overdueDays;
        
        // 检查是否已发送过该天数的通知
        Boolean hasNotified = redisTemplate.hasKey(notifyKey);
        if (Boolean.TRUE.equals(hasNotified)) {
            return;  // 已通知过，跳过
        }
        
        // 构建通知内容
        String title = "订单逾期提醒";
        String buyerContent = String.format("您的订单「%s」已逾期 %d 天，请关注项目进度并与服务商沟通。", 
                order.getTitle(), overdueDays);
        String sellerContent = String.format("您承接的订单「%s」已逾期 %d 天，请尽快完成交付以避免违约。", 
                order.getTitle(), overdueDays);
        
        // 向买家（用户）发送通知
        if (order.getBuyerId() != null) {
            notificationService.sendNotification(
                order.getBuyerId(), 
                "order", 
                title, 
                buyerContent, 
                "order", 
                order.getId()
            );
            log.info("已向买家 {} 发送订单 {} 逾期通知", order.getBuyerId(), order.getId());
        }
        
        // 向卖家（商家）发送通知
        if (order.getSellerId() != null) {
            notificationService.sendNotification(
                order.getSellerId(), 
                "order", 
                title, 
                sellerContent, 
                "order", 
                order.getId()
            );
            log.info("已向卖家 {} 发送订单 {} 逾期通知", order.getSellerId(), order.getId());
        }
        
        // 记录已发送通知，24小时后过期（即第二天可以再次发送）
        redisTemplate.opsForValue().set(notifyKey, true, 24, TimeUnit.HOURS);
    }

    /**
     * 每天早上9点检查即将到期的订单（提前1天提醒）
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkUpcomingDeadlines() {
        log.info("开始检查即将到期订单...");
        
        try {
            // 查询明天到期的订单
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
            LocalDateTime dayAfterTomorrow = LocalDateTime.now().plusDays(2);
            
            LambdaQueryWrapper<TfOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(TfOrder::getStatus, 2, 3)  // 进行中或待验收
                   .isNotNull(TfOrder::getDeliveryTime)
                   .ge(TfOrder::getDeliveryTime, tomorrow)
                   .lt(TfOrder::getDeliveryTime, dayAfterTomorrow);
            
            List<TfOrder> upcomingOrders = orderMapper.selectList(wrapper);
            
            log.info("发现 {} 个即将到期订单", upcomingOrders.size());
            
            for (TfOrder order : upcomingOrders) {
                processUpcomingDeadline(order);
            }
            
            log.info("即将到期订单检查完成");
        } catch (Exception e) {
            log.error("检查即将到期订单时发生错误", e);
        }
    }

    /**
     * 处理即将到期的订单
     */
    private void processUpcomingDeadline(TfOrder order) {
        // 检查订单是否存在质保款阶段，存在则跳过不发送到期提醒
        LambdaQueryWrapper<TfOrderStage> stageWrapper = new LambdaQueryWrapper<>();
        stageWrapper.eq(TfOrderStage::getOrderId, order.getId())
                    .eq(TfOrderStage::getStageType, TfOrderStage.TYPE_WARRANTY);
        Long warrantyCount = stageMapper.selectCount(stageWrapper);
        if (warrantyCount != null && warrantyCount > 0) {
            log.debug("订单 {} 存在质保款阶段，跳过到期提醒", order.getId());
            return;
        }
        
        String notifyKey = OVERDUE_NOTIFY_KEY_PREFIX + "upcoming:" + order.getId();
        
        // 检查是否已发送过提醒
        Boolean hasNotified = redisTemplate.hasKey(notifyKey);
        if (Boolean.TRUE.equals(hasNotified)) {
            return;
        }
        
        String title = "订单即将到期提醒";
        String buyerContent = String.format("您的订单「%s」将于明天到期，请关注项目交付进度。", order.getTitle());
        String sellerContent = String.format("您承接的订单「%s」将于明天到期，请确保按时完成交付。", order.getTitle());
        
        // 向买家发送通知
        if (order.getBuyerId() != null) {
            notificationService.sendNotification(
                order.getBuyerId(),
                "order",
                title,
                buyerContent,
                "order",
                order.getId()
            );
        }
        
        // 向卖家发送通知
        if (order.getSellerId() != null) {
            notificationService.sendNotification(
                order.getSellerId(),
                "order",
                title,
                sellerContent,
                "order",
                order.getId()
            );
        }
        
        // 记录已发送，24小时后过期
        redisTemplate.opsForValue().set(notifyKey, true, 24, TimeUnit.HOURS);
        log.info("已发送订单 {} 即将到期提醒", order.getId());
    }
}
