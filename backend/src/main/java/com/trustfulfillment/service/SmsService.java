package com.trustfulfillment.service;

/**
 * 短信服务接口
 */
public interface SmsService {

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param type  验证码类型: 1登录/注册, 2绑定手机, 3修改密码, 4支付验证
     * @param ip    请求IP
     * @return 是否发送成功
     */
    boolean sendVerifyCode(String phone, Integer type, String ip);

    /**
     * 验证验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @param type  验证码类型
     * @return 是否验证通过
     */
    boolean verifyCode(String phone, String code, Integer type);

    /**
     * 检查发送频率限制
     *
     * @param phone 手机号
     * @param ip    请求IP
     * @return 是否可以发送
     */
    boolean checkRateLimit(String phone, String ip);

    /**
     * 获取今日发送次数
     *
     * @param phone 手机号
     * @return 发送次数
     */
    int getTodaySendCount(String phone);
}
