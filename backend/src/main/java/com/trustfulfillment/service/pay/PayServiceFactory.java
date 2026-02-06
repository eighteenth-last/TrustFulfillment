package com.trustfulfillment.service.pay;

import com.trustfulfillment.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 支付服务工厂
 */
@Component
public class PayServiceFactory {

    private final Map<String, PayService> payServiceMap;

    @Autowired
    private ConfigService configService;

    @Autowired
    public PayServiceFactory(Map<String, PayService> payServiceMap) {
        this.payServiceMap = payServiceMap;
    }

    /**
     * 根据支付类型获取对应的支付服务
     */
    public PayService getService(String payType) {
        if (!isPaymentEnabled(payType)) {
            throw new IllegalArgumentException("支付方式未启用: " + payType);
        }
        
        String beanName = payType + "Service";
        PayService service = payServiceMap.get(beanName);
        
        if (service == null) {
            throw new IllegalArgumentException("不支持的支付类型: " + payType);
        }
        
        return service;
    }

    /**
     * 获取虚拟支付服务
     */
    public PayService getMockService() {
        return payServiceMap.get("mockPayService");
    }

    /**
     * 检查支付方式是否启用
     */
    private boolean isPaymentEnabled(String payType) {
        try {
            // 首先检查激活状态
            if (!configService.isPaymentActive(payType)) {
                return false;
            }
            
            // 然后检查配置中的 enabled 字段
            Map<String, Object> payConfig;
            if ("alipay".equals(payType)) {
                payConfig = configService.getAlipayConfig();
            } else if ("wechat".equals(payType)) {
                payConfig = configService.getWechatConfig();
            } else {
                return false;
            }
            
            if (payConfig == null || payConfig.isEmpty()) {
                return false;
            }
            
            Object enabled = payConfig.get("enabled");
            return enabled != null && (Boolean) enabled;
        } catch (Exception e) {
            return false;
        }
    }
}
