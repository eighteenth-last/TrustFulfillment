package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付配置管理控制器
 */
@RestController
@RequestMapping("/api/admin/pay")
public class AdminPayController {

    private static final Logger log = LoggerFactory.getLogger(AdminPayController.class);

    @Autowired
    private ConfigService configService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取支付配置
     */
    @GetMapping("/config")
    public Result<Map<String, Object>> getPayConfig() {
        try {
            // 检查是否登录
            if (!StpUtil.isLogin()) {
                return Result.error("请先登录");
            }
            
            log.info("开始获取支付配置");
            
            Map<String, Object> config = new HashMap<>();
            
            // 获取支付宝配置
            Map<String, Object> alipayConfig = configService.getAlipayConfig();
            log.info("支付宝配置: {}", alipayConfig);
            config.put("alipay", alipayConfig);
            
            // 获取微信配置
            Map<String, Object> wechatConfig = configService.getWechatConfig();
            log.info("微信配置: {}", wechatConfig);
            config.put("wechat", wechatConfig);
            
            // 获取激活状态
            Map<String, Boolean> activeStatus = configService.getPaymentActiveStatus();
            log.info("激活状态: {}", activeStatus);
            config.put("active", activeStatus);
            
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取支付配置失败", e);
            return Result.error("获取支付配置失败: " + e.getMessage());
        }
    }

    /**
     * 保存支付配置
     */
    @PostMapping("/config")
    public Result<Void> savePayConfig(@RequestBody Map<String, Object> config) {
        try {
            // 检查是否登录
            if (!StpUtil.isLogin()) {
                return Result.error("请先登录");
            }
            
            log.info("开始保存支付配置: {}", config);
            
            // 保存支付宝配置
            if (config.containsKey("alipay")) {
                String alipayJson = objectMapper.writeValueAsString(config.get("alipay"));
                log.info("保存支付宝配置: {}", alipayJson);
                configService.set("payment.alipay", alipayJson, "支付宝支付配置");
            }
            
            // 保存微信配置
            if (config.containsKey("wechat")) {
                String wechatJson = objectMapper.writeValueAsString(config.get("wechat"));
                log.info("保存微信配置: {}", wechatJson);
                configService.set("payment.wechat", wechatJson, "微信支付配置");
            }
            
            // 保存激活状态
            if (config.containsKey("active")) {
                String activeJson = objectMapper.writeValueAsString(config.get("active"));
                log.info("保存激活状态: {}", activeJson);
                configService.set("payment.active", activeJson, "支付方式激活状态");
            }
            
            // 刷新配置缓存
            configService.refreshCache();
            
            log.info("支付配置保存成功");
            return Result.success();
        } catch (Exception e) {
            log.error("保存支付配置失败", e);
            return Result.error("保存支付配置失败: " + e.getMessage());
        }
    }
}
