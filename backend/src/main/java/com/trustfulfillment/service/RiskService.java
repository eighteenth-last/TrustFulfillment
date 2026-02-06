package com.trustfulfillment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trustfulfillment.entity.TfRiskEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 风险监控服务接口
 */
public interface RiskService {
    
    /**
     * 记录风险事件
     */
    TfRiskEvent recordRiskEvent(String eventType, String riskLevel, Long userId, Long orderId, 
            Long disputeId, String title, String description, BigDecimal amount, 
            String ipAddress, String userAgent);
    
    /**
     * 检测大额交易风险
     * @return 如果存在风险返回风险事件，否则返回null
     */
    TfRiskEvent checkLargeTransaction(Long userId, Long orderId, BigDecimal amount);
    
    /**
     * 检测频繁操作风险
     */
    TfRiskEvent checkFrequentOperation(Long userId, String operationType, String ipAddress);
    
    /**
     * 检测纠纷风险
     */
    TfRiskEvent checkDisputeRisk(Long disputeId, Long orderId, BigDecimal amount);
    
    /**
     * 获取风险统计
     */
    Map<String, Object> getRiskStats();
    
    /**
     * 获取风险告警列表
     */
    List<TfRiskEvent> getRiskAlerts(int limit);
    
    /**
     * 获取风险趋势数据
     */
    List<Map<String, Object>> getRiskTrend(int days);
    
    /**
     * 分页查询风险事件
     */
    IPage<TfRiskEvent> getRiskEventList(int page, int size, String eventType, String riskLevel, Integer status);
    
    /**
     * 处理风险事件
     */
    boolean processRiskEvent(Long eventId, Integer status, String remark, Long processedBy);
    
    /**
     * 获取风控规则配置
     */
    Map<String, Boolean> getRiskRules();
    
    /**
     * 保存风控规则配置
     */
    void saveRiskRules(Map<String, Boolean> rules);
    
    /**
     * 检查风控规则是否启用
     */
    boolean isRuleEnabled(String ruleKey);
}
