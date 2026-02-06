package com.trustfulfillment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustfulfillment.entity.SysConfig;
import com.trustfulfillment.mapper.SysConfigMapper;
import com.trustfulfillment.service.ConfigService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统配置服务实现
 */
@Service
public class ConfigServiceImpl implements ConfigService, InitializingBean {

    @Autowired
    private SysConfigMapper configMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 配置缓存
     */
    private final Map<String, String> configCache = new ConcurrentHashMap<>();

    /**
     * 初始化时加载配置到缓存
     */
    @Override
    public void afterPropertiesSet() {
        refreshCache();
    }

    @Override
    public void refreshCache() {
        configCache.clear();
        List<SysConfig> configs = configMapper.selectList(null);
        for (SysConfig config : configs) {
            configCache.put(config.getConfigKey(), config.getConfigValue());
        }
    }

    @Override
    public String getString(String key) {
        return getString(key, null);
    }

    @Override
    public String getString(String key, String defaultValue) {
        String value = configCache.get(key);
        if (value == null) {
            // 尝试从数据库获取
            value = configMapper.getValueByKey(key);
            if (value != null) {
                configCache.put(key, value);
            }
        }
        return value != null ? value : defaultValue;
    }

    @Override
    public Integer getInt(String key) {
        return getInt(key, null);
    }

    @Override
    public Integer getInt(String key, Integer defaultValue) {
        String value = getString(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    @Override
    public BigDecimal getDecimal(String key) {
        return getDecimal(key, null);
    }

    @Override
    public BigDecimal getDecimal(String key, BigDecimal defaultValue) {
        String value = getString(key);
        if (value != null) {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    @Override
    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = getString(key);
        if (value != null) {
            return "true".equalsIgnoreCase(value) || "1".equals(value);
        }
        return defaultValue;
    }

    @Override
    public void set(String key, String value) {
        set(key, value, null);
    }

    @Override
    public void set(String key, String value, String description) {
        SysConfig config = configMapper.selectOne(
                new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));

        if (config != null) {
            config.setConfigValue(value);
            if (description != null) {
                config.setDescription(description);
            }
            config.setUpdateTime(LocalDateTime.now());
            configMapper.updateById(config);
        } else {
            config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setDescription(description);
            config.setCreateTime(LocalDateTime.now());
            config.setUpdateTime(LocalDateTime.now());
            configMapper.insert(config);
        }

        // 更新缓存
        configCache.put(key, value);
    }

    @Override
    public Map<String, String> getAllConfigs() {
        return new HashMap<>(configCache);
    }

    @Override
    public void saveConfigs(Map<String, Object> configs) {
        for (Map.Entry<String, Object> entry : configs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                set(key, String.valueOf(value));
            }
        }
    }

    // ============ 业务常用配置快捷方法 ============

    @Override
    public BigDecimal getPlatformFeeRate() {
        return getDecimal("platform_fee_rate", new BigDecimal("3.5"));
    }

    @Override
    public BigDecimal getWithdrawFeeRate() {
        return getDecimal("withdraw_fee_rate", new BigDecimal("0.1"));
    }

    @Override
    public BigDecimal getWithdrawMinAmount() {
        return getDecimal("withdraw_min_amount", new BigDecimal("100"));
    }

    @Override
    public BigDecimal getMaxSinglePayment() {
        return getDecimal("max_single_payment", new BigDecimal("200000"));
    }

    @Override
    public Integer getAcceptTimeout() {
        return getInt("accept_timeout", 48);
    }

    @Override
    @Deprecated
    public Map<String, Object> getPaymentConfig() {
        // 兼容旧版本，返回合并的配置
        Map<String, Object> config = new HashMap<>();
        config.put("alipay", getAlipayConfig());
        config.put("wechat", getWechatConfig());
        return config;
    }

    @Override
    public Map<String, Object> getAlipayConfig() {
        String configJson = getString("payment.alipay");
        if (configJson == null || configJson.isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getWechatConfig() {
        String configJson = getString("payment.wechat");
        if (configJson == null || configJson.isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Boolean> getPaymentActiveStatus() {
        String configJson = getString("payment.active");
        if (configJson == null || configJson.isEmpty()) {
            Map<String, Boolean> defaultStatus = new HashMap<>();
            defaultStatus.put("alipay", false);
            defaultStatus.put("wechat", false);
            return defaultStatus;
        }
        
        try {
            return objectMapper.readValue(configJson, new TypeReference<Map<String, Boolean>>() {});
        } catch (Exception e) {
            Map<String, Boolean> defaultStatus = new HashMap<>();
            defaultStatus.put("alipay", false);
            defaultStatus.put("wechat", false);
            return defaultStatus;
        }
    }

    @Override
    public boolean isPaymentActive(String payType) {
        Map<String, Boolean> activeStatus = getPaymentActiveStatus();
        return Boolean.TRUE.equals(activeStatus.get(payType));
    }
}
