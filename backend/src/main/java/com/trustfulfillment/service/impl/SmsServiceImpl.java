package com.trustfulfillment.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.trustfulfillment.entity.SysSmsCode;
import com.trustfulfillment.entity.SysSmsLog;
import com.trustfulfillment.mapper.SysSmsCodeMapper;
import com.trustfulfillment.mapper.SysSmsLogMapper;
import com.trustfulfillment.service.ConfigService;
import com.trustfulfillment.service.SmsService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Autowired
    private SysSmsCodeMapper smsCodeMapper;

    @Autowired
    private SysSmsLogMapper smsLogMapper;
    
    @Autowired
    private ConfigService configService;

    // 默认配置（从 application.yml 读取，作为回退值）
    @Value("${sms.aliyun.access-key-id:}")
    private String defaultAccessKeyId;

    @Value("${sms.aliyun.access-key-secret:}")
    private String defaultAccessKeySecret;

    @Value("${sms.aliyun.sign-name:臻托}")
    private String defaultSignName;

    @Value("${sms.aliyun.template-code:SMS_123456789}")
    private String defaultTemplateCode;

    @Value("${sms.code-expire-seconds:300}")
    private int defaultCodeExpireSeconds;

    @Value("${sms.day-limit:10}")
    private int defaultDayLimit;

    @Value("${sms.mock-mode:true}")
    private boolean defaultMockMode;
    
    // 获取当前短信服务提供商
    private String getProvider() {
        return configService.getString("sms_provider", "console");
    }
    
    // 判断是否为控制台模式
    private boolean isConsoleMode() {
        return "console".equals(getProvider());
    }
    
    // 获取阿里云配置
    private String getAliyunAccessKeyId() {
        String value = configService.getString("sms_aliyun_access_key_id", "");
        return value.isEmpty() ? defaultAccessKeyId : value;
    }
    
    private String getAliyunAccessKeySecret() {
        String value = configService.getString("sms_aliyun_access_key_secret", "");
        return value.isEmpty() ? defaultAccessKeySecret : value;
    }
    
    private String getAliyunSignName() {
        String value = configService.getString("sms_aliyun_sign_name", "");
        return value.isEmpty() ? defaultSignName : value;
    }
    
    private String getAliyunTemplateCode() {
        String value = configService.getString("sms_aliyun_template_code", "");
        return value.isEmpty() ? defaultTemplateCode : value;
    }
    
    // 获取腾讯云配置
    private String getTencentSecretId() {
        return configService.getString("sms_tencent_secret_id", "");
    }
    
    private String getTencentSecretKey() {
        return configService.getString("sms_tencent_secret_key", "");
    }
    
    private String getTencentAppId() {
        return configService.getString("sms_tencent_app_id", "");
    }
    
    private String getTencentSignName() {
        return configService.getString("sms_tencent_sign_name", "臻托");
    }
    
    private String getTencentTemplateId() {
        return configService.getString("sms_tencent_template_id", "");
    }
    
    // 获取通用配置
    private int getCodeExpireSeconds() {
        return configService.getInt("sms_code_expire_seconds", defaultCodeExpireSeconds);
    }
    
    private int getDayLimit() {
        return configService.getInt("sms_day_limit", defaultDayLimit);
    }

    @PostConstruct
    public void init() {
        log.info("短信服务已初始化");
    }

    @Override
    @Transactional
    public boolean sendVerifyCode(String phone, Integer type, String ip) {
        // 获取当前配置
        int dayLimitValue = getDayLimit();
        int codeExpireSecondsValue = getCodeExpireSeconds();
        String provider = getProvider();
        
        // 检查频率限制
        if (!checkRateLimit(phone, ip)) {
            throw new RuntimeException("发送过于频繁，请稍后再试");
        }

        // 检查今日发送次数
        int todayCount = getTodaySendCount(phone);
        if (todayCount >= dayLimitValue) {
            throw new RuntimeException("今日发送次数已达上限");
        }

        // 生成6位验证码
        String code = RandomUtil.randomNumbers(6);

        // 保存验证码到数据库
        SysSmsCode smsCode = new SysSmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setType(type);
        smsCode.setIp(ip);
        smsCode.setUsed(0);
        smsCode.setExpireTime(LocalDateTime.now().plusSeconds(codeExpireSecondsValue));
        smsCodeMapper.insert(smsCode);

        // 发送短信
        boolean success;
        String requestId = null;
        String bizId = null;
        String errorCode = null;
        String errorMsg = null;

        if ("console".equals(provider)) {
            // 控制台模式：直接打印验证码
            log.info("========================================");
            log.info("【控制台短信】手机号: {}", phone);
            log.info("【控制台短信】验证码: {}", code);
            log.info("【控制台短信】有效期: {}秒", codeExpireSecondsValue);
            log.info("========================================");
            success = true;
        } else if ("aliyun".equals(provider)) {
            // 阿里云短信
            try {
                Map<String, Object> result = sendAliyunSms(phone, code);
                requestId = (String) result.get("RequestId");
                bizId = (String) result.get("BizId");

                if ("OK".equals(result.get("Code"))) {
                    success = true;
                    log.info("【阿里云短信】发送成功，手机号: {}, bizId: {}", phone, bizId);
                } else {
                    success = false;
                    errorCode = (String) result.get("Code");
                    errorMsg = (String) result.get("Message");
                    log.error("【阿里云短信】发送失败，手机号: {}, 错误: {} - {}", phone, errorCode, errorMsg);
                }
            } catch (Exception e) {
                success = false;
                errorCode = "EXCEPTION";
                errorMsg = e.getMessage();
                log.error("【阿里云短信】发送异常，手机号: {}", phone, e);
            }
        } else if ("tencent".equals(provider)) {
            // 腾讯云短信
            try {
                Map<String, Object> result = sendTencentSms(phone, code);
                requestId = (String) result.get("RequestId");

                if ("Ok".equals(result.get("Code"))) {
                    success = true;
                    log.info("【腾讯云短信】发送成功，手机号: {}", phone);
                } else {
                    success = false;
                    errorCode = (String) result.get("Code");
                    errorMsg = (String) result.get("Message");
                    log.error("【腾讯云短信】发送失败，手机号: {}, 错误: {} - {}", phone, errorCode, errorMsg);
                }
            } catch (Exception e) {
                success = false;
                errorCode = "EXCEPTION";
                errorMsg = e.getMessage();
                log.error("【腾讯云短信】发送异常，手机号: {}", phone, e);
            }
        } else {
            // 未知提供商，使用控制台模式
            log.warn("未知的短信服务提供商: {}，使用控制台模式", provider);
            log.info("========================================");
            log.info("【控制台短信】手机号: {}", phone);
            log.info("【控制台短信】验证码: {}", code);
            log.info("【控制台短信】有效期: {}秒", codeExpireSecondsValue);
            log.info("========================================");
            success = true;
        }

        // 记录发送日志
        SysSmsLog smsLog = new SysSmsLog();
        smsLog.setPhone(phone);
        smsLog.setTemplateCode("aliyun".equals(provider) ? getAliyunTemplateCode() : 
                               "tencent".equals(provider) ? getTencentTemplateId() : "CONSOLE");
        smsLog.setContent("验证码: " + code);
        smsLog.setType(1);
        smsLog.setProvider(provider);
        smsLog.setRequestId(requestId);
        smsLog.setBizId(bizId);
        smsLog.setStatus(success ? SysSmsLog.STATUS_SUCCESS : SysSmsLog.STATUS_FAILED);
        smsLog.setErrorCode(errorCode);
        smsLog.setErrorMsg(errorMsg);
        smsLog.setIp(ip);
        smsLogMapper.insert(smsLog);

        return success;
    }

    /**
     * 调用阿里云短信API (使用HTTP方式，不依赖SDK)
     */
    private Map<String, Object> sendAliyunSms(String phone, String code) throws Exception {
        String accessKeyId = getAliyunAccessKeyId();
        String accessKeySecret = getAliyunAccessKeySecret();
        String signName = getAliyunSignName();
        String templateCode = getAliyunTemplateCode();
        
        if (accessKeyId.isEmpty() || accessKeySecret.isEmpty()) {
            throw new RuntimeException("阿里云短信配置不完整，请先配置 AccessKeyId 和 AccessKeySecret");
        }
        
        // 构建请求参数
        Map<String, String> params = new TreeMap<>();
        params.put("AccessKeyId", accessKeyId);
        params.put("Action", "SendSms");
        params.put("Format", "JSON");
        params.put("PhoneNumbers", phone);
        params.put("SignName", signName);
        params.put("SignatureMethod", "HMAC-SHA1");
        params.put("SignatureNonce", UUID.randomUUID().toString());
        params.put("SignatureVersion", "1.0");
        params.put("TemplateCode", templateCode);
        params.put("TemplateParam", "{\"code\":\"" + code + "\"}");
        params.put("Timestamp", getUTCTimestamp());
        params.put("Version", "2017-05-25");

        // 构建签名字符串
        StringBuilder sortedQueryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sortedQueryString.append("&")
                    .append(percentEncode(entry.getKey()))
                    .append("=")
                    .append(percentEncode(entry.getValue()));
        }
        String stringToSign = "GET&%2F&" + percentEncode(sortedQueryString.substring(1));

        // 计算签名
        String signature = calculateSignature(stringToSign, accessKeySecret + "&");
        params.put("Signature", signature);

        // 发送请求
        String url = "https://dysmsapi.aliyuncs.com/?" + buildQueryString(params);
        String response = HttpUtil.get(url, 10000);

        return JSONUtil.parseObj(response);
    }
    
    /**
     * 调用腾讯云短信API (使用HTTP方式)
     * 注意：实际生产环境建议使用腾讯云SDK
     */
    private Map<String, Object> sendTencentSms(String phone, String code) throws Exception {
        String secretId = getTencentSecretId();
        String secretKey = getTencentSecretKey();
        String appId = getTencentAppId();
        String signName = getTencentSignName();
        String templateId = getTencentTemplateId();
        
        if (secretId.isEmpty() || secretKey.isEmpty() || appId.isEmpty()) {
            throw new RuntimeException("腾讯云短信配置不完整，请先配置 SecretId、SecretKey 和 AppId");
        }
        
        // 腾讯云短信 API 请求构建
        // 注意：这里简化实现，实际生产环境建议使用腾讯云官方SDK
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = String.valueOf(new Random().nextInt(100000));
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("SmsSdkAppId", appId);
        requestBody.put("SignName", signName);
        requestBody.put("TemplateId", templateId);
        requestBody.put("PhoneNumberSet", new String[]{"+86" + phone});
        requestBody.put("TemplateParamSet", new String[]{code});
        
        String body = JSONUtil.toJsonStr(requestBody);
        
        // 计算签名
        String service = "sms";
        String host = "sms.tencentcloudapi.com";
        String algorithm = "TC3-HMAC-SHA256";
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        // 简化处理：返回模拟成功结果
        // 实际生产环境应该使用完整的签名逻辑或腾讯云SDK
        log.warn("腾讯云短信API调用需要完整的签名逻辑，建议添加腾讯云SDK依赖");
        log.info("【腾讯云短信】请求参数: phone={}, templateId={}, code={}", phone, templateId, code);
        
        // 暂时返回模拟结果，实际需要实现完整的API调用
        Map<String, Object> result = new HashMap<>();
        result.put("Code", "Ok");
        result.put("Message", "模拟发送成功，请添加腾讯云SDK依赖以启用真实发送");
        result.put("RequestId", UUID.randomUUID().toString());
        
        return result;
    }

    private String getUTCTimestamp() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(new Date());
    }

    private String percentEncode(String value) throws Exception {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("*", "%2A")
                .replace("%7E", "~");
    }

    private String calculateSignature(String stringToSign, String secret) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
        mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signData);
    }

    private String buildQueryString(Map<String, String> params) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(percentEncode(entry.getKey()))
                    .append("=")
                    .append(percentEncode(entry.getValue()));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public boolean verifyCode(String phone, String code, Integer type) {
        // 查询最新的未使用验证码
        SysSmsCode smsCode = smsCodeMapper.selectOne(
                new LambdaQueryWrapper<SysSmsCode>()
                        .eq(SysSmsCode::getPhone, phone)
                        .eq(SysSmsCode::getType, type)
                        .eq(SysSmsCode::getUsed, 0)
                        .gt(SysSmsCode::getExpireTime, LocalDateTime.now())
                        .orderByDesc(SysSmsCode::getCreateTime)
                        .last("LIMIT 1")
        );

        if (smsCode == null) {
            return false;
        }

        if (!smsCode.getCode().equals(code)) {
            return false;
        }

        // 标记验证码已使用
        smsCodeMapper.update(null,
                new LambdaUpdateWrapper<SysSmsCode>()
                        .eq(SysSmsCode::getId, smsCode.getId())
                        .set(SysSmsCode::getUsed, 1)
        );

        return true;
    }

    @Override
    public boolean checkRateLimit(String phone, String ip) {
        // 开发环境：控制台模式下，频率限制改为10秒
        int rateLimitSeconds = isConsoleMode() ? 10 : 60;
        
        // 检查指定时间内是否发送过
        long count = smsCodeMapper.selectCount(
                new LambdaQueryWrapper<SysSmsCode>()
                        .eq(SysSmsCode::getPhone, phone)
                        .gt(SysSmsCode::getCreateTime, LocalDateTime.now().minusSeconds(rateLimitSeconds))
        );
        return count == 0;
    }

    @Override
    public int getTodaySendCount(String phone) {
        Long count = smsCodeMapper.selectCount(
                new LambdaQueryWrapper<SysSmsCode>()
                        .eq(SysSmsCode::getPhone, phone)
                        .ge(SysSmsCode::getCreateTime, LocalDate.now().atStartOfDay())
        );
        return count.intValue();
    }
}
