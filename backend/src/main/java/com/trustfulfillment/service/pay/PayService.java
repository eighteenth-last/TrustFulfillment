package com.trustfulfillment.service.pay;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付服务接口
 */
public interface PayService {

    /**
     * 获取支付类型
     * @return 支付类型标识 (alipay, wechat, mock)
     */
    String getPayType();

    /**
     * 创建支付订单
     * @param orderNo 订单号
     * @param amount 支付金额
     * @param subject 订单标题
     * @return 支付参数
     */
    Map<String, Object> createPayOrder(String orderNo, BigDecimal amount, String subject);

    /**
     * 处理支付回调
     * @param request HTTP请求
     * @return 回调结果 (orderNo, payNo, amount)
     */
    Map<String, String> processNotify(HttpServletRequest request) throws Exception;

    /**
     * 查询支付状态
     * @param orderNo 订单号
     * @return 是否已支付
     */
    boolean queryPayStatus(String orderNo);
}
