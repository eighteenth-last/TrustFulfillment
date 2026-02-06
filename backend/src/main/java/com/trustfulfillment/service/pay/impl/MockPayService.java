package com.trustfulfillment.service.pay.impl;

import com.trustfulfillment.service.pay.AbstractPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 虚拟支付服务实现（测试用）
 */
@Service("mockPayService")
public class MockPayService extends AbstractPayService {

    private static final Logger log = LoggerFactory.getLogger(MockPayService.class);

    @Override
    public String getPayType() {
        return "mock";
    }

    @Override
    public Map<String, Object> createPayOrder(String orderNo, BigDecimal amount, String subject) {
        Map<String, Object> result = new HashMap<>();
        
        log.info("【虚拟支付】创建支付订单: orderNo={}, amount={}, subject={}", orderNo, amount, subject);
        
        String mockQRCode = generateMockQRCode(orderNo, amount, subject);
        
        result.put("success", true);
        result.put("payType", "mock");
        result.put("qrCode", mockQRCode);
        result.put("orderNo", orderNo);
        result.put("message", "测试模式：支付将在3秒后自动完成");
        
        log.info("【虚拟支付】订单创建成功，将在3秒后自动完成支付");
        
        return result;
    }

    @Override
    public Map<String, String> processNotify(HttpServletRequest request) throws Exception {
        Map<String, String> result = new HashMap<>();
        result.put("orderNo", request.getParameter("orderNo"));
        result.put("payNo", "MOCK" + System.currentTimeMillis());
        result.put("amount", request.getParameter("amount"));
        
        log.info("【虚拟支付】处理支付回调: {}", result);
        return result;
    }

    @Override
    public boolean queryPayStatus(String orderNo) {
        log.info("【虚拟支付】查询支付状态: orderNo={}, 返回成功", orderNo);
        return true;
    }

    private String generateMockQRCode(String orderNo, BigDecimal amount, String subject) {
        String content = String.format("【测试支付】\n订单号: %s\n金额: ¥%s\n说明: %s\n\n此为测试模式，支付将自动完成", 
            orderNo, amount, subject);
        
        try {
            return generateQRCode(content);
        } catch (Exception e) {
            log.error("生成虚拟支付二维码失败", e);
            return "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2YwZjBmMCIvPgogIDx0ZXh0IHg9IjE1MCIgeT0iMTUwIiBmb250LXNpemU9IjIwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjNjY2Ij7mtYvor5XmqKHlvI88L3RleHQ+Cjwvc3ZnPg==";
        }
    }
}
