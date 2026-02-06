package com.trustfulfillment.service.pay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustfulfillment.service.ConfigService;
import com.trustfulfillment.service.pay.AbstractPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付服务实现
 */
@Service("alipayService")
public class AlipayService extends AbstractPayService {

    private static final Logger log = LoggerFactory.getLogger(AlipayService.class);

    @Autowired
    private ConfigService configService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getPayType() {
        return "alipay";
    }

    @Override
    public Map<String, Object> createPayOrder(String orderNo, BigDecimal amount, String subject) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> alipayConfig = configService.getAlipayConfig();
            
            if (alipayConfig == null || alipayConfig.isEmpty()) {
                throw new RuntimeException("支付宝配置不存在");
            }
            
            AlipayClient alipayClient = new DefaultAlipayClient(
                (String) alipayConfig.get("gatewayUrl"),
                (String) alipayConfig.get("appId"),
                (String) alipayConfig.get("privateKey"),
                "json",
                "UTF-8",
                (String) alipayConfig.get("publicKey"),
                (String) alipayConfig.get("signType")
            );
            
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setNotifyUrl((String) alipayConfig.get("notifyUrl"));
            request.setReturnUrl((String) alipayConfig.get("returnUrl"));
            
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(orderNo);
            model.setTotalAmount(amount.toString());
            model.setSubject(subject);
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            
            request.setBizModel(model);
            
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            
            if (response.isSuccess()) {
                result.put("success", true);
                result.put("form", response.getBody());
                result.put("orderNo", orderNo);
                log.info("支付宝订单创建成功: orderNo={}", orderNo);
            } else {
                result.put("success", false);
                result.put("message", response.getMsg());
                log.error("支付宝订单创建失败: {}", response.getMsg());
            }
            
        } catch (Exception e) {
            log.error("创建支付宝订单失败", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, String> processNotify(HttpServletRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        
        Map<String, Object> alipayConfig = configService.getAlipayConfig();
        
        boolean signVerified = AlipaySignature.rsaCheckV1(
            params,
            (String) alipayConfig.get("publicKey"),
            "UTF-8",
            (String) alipayConfig.get("signType")
        );
        
        if (!signVerified) {
            throw new RuntimeException("支付宝回调验签失败");
        }
        
        String tradeStatus = params.get("trade_status");
        if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
            throw new RuntimeException("支付未成功: " + tradeStatus);
        }
        
        Map<String, String> result = new HashMap<>();
        result.put("orderNo", params.get("out_trade_no"));
        result.put("payNo", params.get("trade_no"));
        result.put("amount", params.get("total_amount"));
        
        return result;
    }

    @Override
    public boolean queryPayStatus(String orderNo) {
        try {
            Map<String, Object> alipayConfig = configService.getAlipayConfig();
            
            AlipayClient alipayClient = new DefaultAlipayClient(
                (String) alipayConfig.get("gatewayUrl"),
                (String) alipayConfig.get("appId"),
                (String) alipayConfig.get("privateKey"),
                "json",
                "UTF-8",
                (String) alipayConfig.get("publicKey"),
                (String) alipayConfig.get("signType")
            );
            
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(orderNo);
            request.setBizModel(model);
            
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            
            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();
                return "TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus);
            }
            
            return false;
        } catch (AlipayApiException e) {
            log.error("查询支付宝支付状态失败", e);
            return false;
        }
    }
}
