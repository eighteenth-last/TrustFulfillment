package com.trustfulfillment.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 系统配置服务接口
 */
public interface ConfigService {

    /**
     * 获取字符串配置
     */
    String getString(String key);

    /**
     * 获取字符串配置，带默认值
     */
    String getString(String key, String defaultValue);

    /**
     * 获取整数配置
     */
    Integer getInt(String key);

    /**
     * 获取整数配置，带默认值
     */
    Integer getInt(String key, Integer defaultValue);

    /**
     * 获取BigDecimal配置
     */
    BigDecimal getDecimal(String key);

    /**
     * 获取BigDecimal配置，带默认值
     */
    BigDecimal getDecimal(String key, BigDecimal defaultValue);

    /**
     * 获取布尔配置
     */
    Boolean getBoolean(String key);

    /**
     * 获取布尔配置，带默认值
     */
    Boolean getBoolean(String key, Boolean defaultValue);

    /**
     * 设置配置值
     */
    void set(String key, String value);

    /**
     * 设置配置值，带描述
     */
    void set(String key, String value, String description);

    /**
     * 批量获取所有配置
     */
    Map<String, String> getAllConfigs();

    /**
     * 批量保存配置
     */
    void saveConfigs(Map<String, Object> configs);

    /**
     * 刷新配置缓存
     */
    void refreshCache();

    // ============ 业务常用配置快捷方法 ============

    /**
     * 获取平台服务费率(%)
     */
    BigDecimal getPlatformFeeRate();

    /**
     * 获取提现手续费率(%)
     */
    BigDecimal getWithdrawFeeRate();

    /**
     * 获取最低提现金额
     */
    BigDecimal getWithdrawMinAmount();

    /**
     * 获取单笔支付上限
     */
    BigDecimal getMaxSinglePayment();

    /**
     * 获取验收超时时间(小时)
     */
    Integer getAcceptTimeout();

    /**
     * 获取支付配置（JSON格式）
     * @deprecated 使用 getAlipayConfig() 和 getWechatConfig() 代替
     */
    @Deprecated
    Map<String, Object> getPaymentConfig();

    /**
     * 获取支付宝配置
     */
    Map<String, Object> getAlipayConfig();

    /**
     * 获取微信支付配置
     */
    Map<String, Object> getWechatConfig();

    /**
     * 获取支付激活状态
     */
    Map<String, Boolean> getPaymentActiveStatus();

    /**
     * 检查支付方式是否已激活
     */
    boolean isPaymentActive(String payType);
}
