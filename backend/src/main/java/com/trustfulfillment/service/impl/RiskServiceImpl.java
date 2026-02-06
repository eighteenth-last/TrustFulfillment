package com.trustfulfillment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.TfDispute;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.entity.TfRiskEvent;
import com.trustfulfillment.mapper.TfDisputeMapper;
import com.trustfulfillment.mapper.TfOrderMapper;
import com.trustfulfillment.mapper.TfRiskEventMapper;
import com.trustfulfillment.service.ConfigService;
import com.trustfulfillment.service.RiskService;
import com.trustfulfillment.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 风险监控服务实现
 */
@Service
public class RiskServiceImpl implements RiskService {

    @Autowired
    private TfRiskEventMapper riskEventMapper;
    
    @Autowired
    private TfOrderMapper orderMapper;
    
    @Autowired
    private TfDisputeMapper disputeMapper;
    
    @Autowired
    private SysUserService userService;
    
    @Autowired
    private ConfigService configService;
    
    // 风控规则配置键
    private static final String RULE_ANTIFRAUD = "risk_rule_antifraud";
    private static final String RULE_ACCOUNT_ANOMALY = "risk_rule_account_anomaly";
    private static final String RULE_LARGE_TRANSACTION = "risk_rule_large_transaction";
    private static final String RULE_FREQUENT_OPERATION = "risk_rule_frequent_operation";
    private static final String RULE_IP_RISK = "risk_rule_ip_risk";
    
    // 风控阈值配置键
    private static final String THRESHOLD_LARGE_AMOUNT = "risk_threshold_large_amount";
    private static final String THRESHOLD_FREQUENT_COUNT = "risk_threshold_frequent_count";
    private static final String THRESHOLD_FREQUENT_MINUTES = "risk_threshold_frequent_minutes";

    @Override
    public TfRiskEvent recordRiskEvent(String eventType, String riskLevel, Long userId, Long orderId,
            Long disputeId, String title, String description, BigDecimal amount,
            String ipAddress, String userAgent) {
        
        TfRiskEvent event = new TfRiskEvent();
        event.setEventType(eventType);
        event.setRiskLevel(riskLevel);
        event.setUserId(userId);
        event.setOrderId(orderId);
        event.setDisputeId(disputeId);
        event.setTitle(title);
        event.setDescription(description);
        event.setAmount(amount);
        event.setIpAddress(ipAddress);
        event.setUserAgent(userAgent);
        event.setStatus(TfRiskEvent.STATUS_PENDING);
        
        riskEventMapper.insert(event);
        return event;
    }

    @Override
    public TfRiskEvent checkLargeTransaction(Long userId, Long orderId, BigDecimal amount) {
        if (!isRuleEnabled(RULE_LARGE_TRANSACTION)) {
            return null;
        }
        
        // 获取大额交易阈值，默认10万
        BigDecimal threshold = configService.getDecimal(THRESHOLD_LARGE_AMOUNT, new BigDecimal("100000"));
        
        if (amount != null && amount.compareTo(threshold) >= 0) {
            String title = "大额交易预警";
            String description = String.format("用户发起大额交易，金额: ¥%s，超过阈值 ¥%s", 
                    amount.toString(), threshold.toString());
            
            return recordRiskEvent(TfRiskEvent.TYPE_LARGE_TRANSACTION, TfRiskEvent.LEVEL_MEDIUM,
                    userId, orderId, null, title, description, amount, null, null);
        }
        return null;
    }

    @Override
    public TfRiskEvent checkFrequentOperation(Long userId, String operationType, String ipAddress) {
        if (!isRuleEnabled(RULE_FREQUENT_OPERATION)) {
            return null;
        }
        
        // 获取频繁操作阈值
        int countThreshold = configService.getInt(THRESHOLD_FREQUENT_COUNT, 10);
        int minutesThreshold = configService.getInt(THRESHOLD_FREQUENT_MINUTES, 5);
        
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(minutesThreshold);
        Long count = riskEventMapper.countUserOperations(userId, operationType, startTime);
        
        if (count != null && count >= countThreshold) {
            String title = "频繁操作预警";
            String description = String.format("用户在 %d 分钟内进行了 %d 次 %s 操作", 
                    minutesThreshold, count, operationType);
            
            return recordRiskEvent(TfRiskEvent.TYPE_FREQUENT_OPERATION, TfRiskEvent.LEVEL_LOW,
                    userId, null, null, title, description, null, ipAddress, null);
        }
        return null;
    }

    @Override
    public TfRiskEvent checkDisputeRisk(Long disputeId, Long orderId, BigDecimal amount) {
        // 大额纠纷阈值10万
        BigDecimal largeDisputeThreshold = new BigDecimal("100000");
        
        String riskLevel = TfRiskEvent.LEVEL_MEDIUM;
        if (amount != null && amount.compareTo(largeDisputeThreshold) >= 0) {
            riskLevel = TfRiskEvent.LEVEL_HIGH;
        }
        
        String title = riskLevel.equals(TfRiskEvent.LEVEL_HIGH) ? "大额订单纠纷" : "订单纠纷";
        String description = String.format("订单产生纠纷，涉及金额: ¥%s", 
                amount != null ? amount.toString() : "0");
        
        return recordRiskEvent(TfRiskEvent.TYPE_DISPUTE, riskLevel,
                null, orderId, disputeId, title, description, amount, null, null);
    }

    @Override
    public Map<String, Object> getRiskStats() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        // 今日拦截（状态为已拦截的事件）
        Long todayBlocked = riskEventMapper.countByStatusAndDateRange(
                TfRiskEvent.STATUS_BLOCKED, todayStart, todayEnd);
        stats.put("todayBlocked", todayBlocked != null ? todayBlocked : 0L);
        
        // 待处理风险订单
        Long pendingEvents = riskEventMapper.selectCount(new LambdaQueryWrapper<TfRiskEvent>()
                .eq(TfRiskEvent::getStatus, TfRiskEvent.STATUS_PENDING));
        stats.put("riskOrders", pendingEvents != null ? pendingEvents : 0L);
        
        // 可疑账户（有高风险事件的不同用户数）
        LambdaQueryWrapper<TfRiskEvent> highRiskWrapper = new LambdaQueryWrapper<>();
        highRiskWrapper.eq(TfRiskEvent::getRiskLevel, TfRiskEvent.LEVEL_HIGH)
                .eq(TfRiskEvent::getStatus, TfRiskEvent.STATUS_PENDING)
                .isNotNull(TfRiskEvent::getUserId);
        List<TfRiskEvent> highRiskEvents = riskEventMapper.selectList(highRiskWrapper);
        Set<Long> suspiciousUserIds = new HashSet<>();
        for (TfRiskEvent event : highRiskEvents) {
            if (event.getUserId() != null) {
                suspiciousUserIds.add(event.getUserId());
            }
        }
        stats.put("suspiciousUsers", suspiciousUserIds.size());
        
        // 今日新增事件
        Long todayEvents = riskEventMapper.countByDateRange(todayStart, todayEnd);
        stats.put("todayEvents", todayEvents != null ? todayEvents : 0L);
        
        // 系统安全状态
        Long highRiskCount = riskEventMapper.selectCount(new LambdaQueryWrapper<TfRiskEvent>()
                .eq(TfRiskEvent::getRiskLevel, TfRiskEvent.LEVEL_HIGH)
                .eq(TfRiskEvent::getStatus, TfRiskEvent.STATUS_PENDING));
        String safetyStatus = "safe";
        if (highRiskCount != null && highRiskCount > 5) {
            safetyStatus = "danger";
        } else if (highRiskCount != null && highRiskCount > 0) {
            safetyStatus = "warning";
        }
        stats.put("safetyStatus", safetyStatus);
        
        return stats;
    }

    @Override
    public List<TfRiskEvent> getRiskAlerts(int limit) {
        LambdaQueryWrapper<TfRiskEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfRiskEvent::getStatus, TfRiskEvent.STATUS_PENDING)
                .orderByDesc(TfRiskEvent::getRiskLevel) // 高风险优先
                .orderByDesc(TfRiskEvent::getCreateTime)
                .last("LIMIT " + limit);
        
        List<TfRiskEvent> events = riskEventMapper.selectList(wrapper);
        
        // 填充关联信息
        for (TfRiskEvent event : events) {
            if (event.getUserId() != null) {
                SysUser user = userService.getById(event.getUserId());
                if (user != null) {
                    event.setUserName(user.getNickname());
                }
            }
            if (event.getOrderId() != null) {
                TfOrder order = orderMapper.selectById(event.getOrderId());
                if (order != null) {
                    event.setOrderNo(order.getOrderNo());
                }
            }
        }
        
        return events;
    }

    @Override
    public List<Map<String, Object>> getRiskTrend(int days) {
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            Map<String, Object> item = new HashMap<>();
            item.put("date", String.format("%02d-%02d", date.getMonthValue(), date.getDayOfMonth()));
            
            // 统计各风险等级的事件数
            Long high = riskEventMapper.countByLevelAndDateRange(TfRiskEvent.LEVEL_HIGH, dayStart, dayEnd);
            Long medium = riskEventMapper.countByLevelAndDateRange(TfRiskEvent.LEVEL_MEDIUM, dayStart, dayEnd);
            Long low = riskEventMapper.countByLevelAndDateRange(TfRiskEvent.LEVEL_LOW, dayStart, dayEnd);
            
            item.put("high", high != null ? high : 0L);
            item.put("medium", medium != null ? medium : 0L);
            item.put("low", low != null ? low : 0L);
            
            trendData.add(item);
        }
        
        return trendData;
    }

    @Override
    public IPage<TfRiskEvent> getRiskEventList(int page, int size, String eventType, String riskLevel, Integer status) {
        LambdaQueryWrapper<TfRiskEvent> wrapper = new LambdaQueryWrapper<>();
        
        if (eventType != null && !eventType.isEmpty()) {
            wrapper.eq(TfRiskEvent::getEventType, eventType);
        }
        if (riskLevel != null && !riskLevel.isEmpty()) {
            wrapper.eq(TfRiskEvent::getRiskLevel, riskLevel);
        }
        if (status != null) {
            wrapper.eq(TfRiskEvent::getStatus, status);
        }
        
        wrapper.orderByDesc(TfRiskEvent::getCreateTime);
        
        IPage<TfRiskEvent> pageResult = riskEventMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 填充关联信息
        for (TfRiskEvent event : pageResult.getRecords()) {
            if (event.getUserId() != null) {
                SysUser user = userService.getById(event.getUserId());
                if (user != null) {
                    event.setUserName(user.getNickname());
                }
            }
            if (event.getOrderId() != null) {
                TfOrder order = orderMapper.selectById(event.getOrderId());
                if (order != null) {
                    event.setOrderNo(order.getOrderNo());
                }
            }
            if (event.getProcessedBy() != null) {
                SysUser processor = userService.getById(event.getProcessedBy());
                if (processor != null) {
                    event.setProcessedByName(processor.getNickname());
                }
            }
        }
        
        return pageResult;
    }

    @Override
    public boolean processRiskEvent(Long eventId, Integer status, String remark, Long processedBy) {
        TfRiskEvent event = riskEventMapper.selectById(eventId);
        if (event == null) {
            return false;
        }
        
        event.setStatus(status);
        event.setProcessRemark(remark);
        event.setProcessedBy(processedBy);
        event.setProcessTime(LocalDateTime.now());
        
        return riskEventMapper.updateById(event) > 0;
    }

    @Override
    public Map<String, Boolean> getRiskRules() {
        Map<String, Boolean> rules = new HashMap<>();
        rules.put("antifraud", configService.getBoolean(RULE_ANTIFRAUD, true));
        rules.put("accountAnomaly", configService.getBoolean(RULE_ACCOUNT_ANOMALY, true));
        rules.put("largeTransaction", configService.getBoolean(RULE_LARGE_TRANSACTION, true));
        rules.put("frequentOperation", configService.getBoolean(RULE_FREQUENT_OPERATION, true));
        rules.put("ipRisk", configService.getBoolean(RULE_IP_RISK, true));
        return rules;
    }

    @Override
    public void saveRiskRules(Map<String, Boolean> rules) {
        if (rules.containsKey("antifraud")) {
            configService.set(RULE_ANTIFRAUD, String.valueOf(rules.get("antifraud")), "智能反欺诈规则");
        }
        if (rules.containsKey("accountAnomaly")) {
            configService.set(RULE_ACCOUNT_ANOMALY, String.valueOf(rules.get("accountAnomaly")), "账户异常检测规则");
        }
        if (rules.containsKey("largeTransaction")) {
            configService.set(RULE_LARGE_TRANSACTION, String.valueOf(rules.get("largeTransaction")), "大额交易审核规则");
        }
        if (rules.containsKey("frequentOperation")) {
            configService.set(RULE_FREQUENT_OPERATION, String.valueOf(rules.get("frequentOperation")), "频繁操作限制规则");
        }
        if (rules.containsKey("ipRisk")) {
            configService.set(RULE_IP_RISK, String.valueOf(rules.get("ipRisk")), "IP地址风控规则");
        }
    }

    @Override
    public boolean isRuleEnabled(String ruleKey) {
        return configService.getBoolean(ruleKey, true);
    }
}
