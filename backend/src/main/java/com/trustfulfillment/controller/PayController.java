package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.TfTransaction;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.mapper.TfTransactionMapper;
import com.trustfulfillment.mapper.SysUserMapper;
import com.trustfulfillment.service.pay.PayService;
import com.trustfulfillment.service.pay.PayServiceFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/pay")
public class PayController {

    private static final Logger log = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private PayServiceFactory payServiceFactory;

    @Autowired
    private TfTransactionMapper transactionMapper;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 创建充值订单
     */
    @PostMapping("/recharge")
    public Result<Map<String, Object>> createRechargeOrder(@RequestBody Map<String, Object> params) {
        try {
            BigDecimal amount = new BigDecimal(params.get("amount").toString());
            String payType = params.get("payType").toString();

            if (amount.compareTo(BigDecimal.ONE) < 0) {
                return Result.error("充值金额不能小于1元");
            }

            Long userId = StpUtil.getLoginIdAsLong();
            String orderNo = "R" + System.currentTimeMillis() + userId;

            TfTransaction transaction = new TfTransaction();
            transaction.setUserId(userId);
            transaction.setType("recharge");
            transaction.setAmount(amount);
            transaction.setTitle("账户充值");
            transaction.setStatus(0);
            transactionMapper.insert(transaction);

            PayService payService;
            try {
                payService = payServiceFactory.getService(payType);
            } catch (IllegalArgumentException e) {
                log.warn("支付方式 {} 未启用，使用虚拟支付", payType);
                payService = payServiceFactory.getMockService();
                payType = "mock";
            }

            Map<String, Object> result = payService.createPayOrder(orderNo, amount, "臻托账户充值");

            if (Boolean.TRUE.equals(result.get("success"))) {
                result.put("orderNo", orderNo);
                result.put("payType", payType);
                
                if ("mock".equals(payType)) {
                    scheduleAutoPayment(orderNo, amount);
                }
                
                return Result.success(result);
            } else {
                return Result.error(result.get("message").toString());
            }
        } catch (Exception e) {
            log.error("创建充值订单失败", e);
            return Result.error("创建充值订单失败: " + e.getMessage());
        }
    }

    /**
     * 支付宝回调
     */
    @PostMapping("/notify/alipay")
    @Transactional
    public String alipayNotify(HttpServletRequest request) {
        try {
            PayService service = payServiceFactory.getService("alipay");
            Map<String, String> result = service.processNotify(request);
            handlePaySuccess(result.get("orderNo"), result.get("payNo"), "alipay");
            return "success";
        } catch (Exception e) {
            log.error("支付宝回调异常", e);
            return "failure";
        }
    }

    /**
     * 微信支付回调
     */
    @PostMapping("/notify/wechat")
    @Transactional
    public Map<String, String> wechatNotify(HttpServletRequest request) {
        Map<String, String> res = new HashMap<>();
        try {
            PayService service = payServiceFactory.getService("wechat");
            Map<String, String> result = service.processNotify(request);
            handlePaySuccess(result.get("orderNo"), result.get("payNo"), "wechat");
            res.put("code", "SUCCESS");
            res.put("message", "成功");
        } catch (Exception e) {
            log.error("微信回调异常", e);
            res.put("code", "FAIL");
            res.put("message", "失败");
        }
        return res;
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{orderNo}")
    public Result<Map<String, Object>> getPaymentStatus(@PathVariable String orderNo) {
        try {
            // 查询交易记录
            LambdaQueryWrapper<TfTransaction> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TfTransaction::getType, "recharge")
                   .orderByDesc(TfTransaction::getCreateTime)
                   .last("LIMIT 1");
            TfTransaction transaction = transactionMapper.selectOne(wrapper);
            
            Map<String, Object> result = new HashMap<>();
            if (transaction != null) {
                if (transaction.getStatus() == 1) {
                    result.put("status", "success");
                } else if (transaction.getStatus() == 2) {
                    result.put("status", "failed");
                } else {
                    result.put("status", "pending");
                }
                result.put("amount", transaction.getAmount());
            } else {
                result.put("status", "not_found");
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询支付状态失败", e);
            return Result.error("查询支付状态失败");
        }
    }

    /**
     * 查询支付状态（旧接口，保持兼容）
     */
    @GetMapping("/query/status")
    public Result<Map<String, Object>> queryPayStatus(@RequestParam String orderNo, @RequestParam String payType) {
        try {
            PayService payService = payServiceFactory.getService(payType);
            boolean isPaid = payService.queryPayStatus(orderNo);

            Map<String, Object> result = new HashMap<>();
            result.put("isPaid", isPaid);
            result.put("orderNo", orderNo);

            return Result.success(result);
        } catch (Exception e) {
            log.error("查询支付状态失败", e);
            return Result.error("查询支付状态失败");
        }
    }

    private void scheduleAutoPayment(String orderNo, BigDecimal amount) {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                log.info("【虚拟支付】自动完成支付: orderNo={}", orderNo);
                handlePaySuccess(orderNo, "MOCK" + System.currentTimeMillis(), "mock");
            } catch (Exception e) {
                log.error("虚拟支付自动完成失败", e);
            }
        }).start();
    }

    private void handlePaySuccess(String orderNo, String payNo, String payType) {
        if (orderNo.startsWith("R")) {
            handleRechargeSuccess(orderNo, payNo, payType);
        }
    }

    private void handleRechargeSuccess(String orderNo, String payNo, String payType) {
        LambdaQueryWrapper<TfTransaction> txWrapper = new LambdaQueryWrapper<>();
        txWrapper.eq(TfTransaction::getType, "recharge")
                 .eq(TfTransaction::getStatus, 0)
                 .orderByDesc(TfTransaction::getCreateTime)
                 .last("LIMIT 1");
        TfTransaction transaction = transactionMapper.selectOne(txWrapper);
        
        if (transaction == null || transaction.getStatus() != 0) {
            return;
        }

        transaction.setStatus(1);
        transaction.setRemark("支付流水号: " + payNo + ", 支付方式: " + payType);
        transactionMapper.updateById(transaction);

        SysUser user = userMapper.selectById(transaction.getUserId());
        if (user != null) {
            BigDecimal newBalance = (user.getBalance() == null ? BigDecimal.ZERO : user.getBalance())
                    .add(transaction.getAmount());
            user.setBalance(newBalance);
            userMapper.updateById(user);
            
            transaction.setBalanceAfter(newBalance);
            transactionMapper.updateById(transaction);
            
            log.info("充值成功: orderNo={}, userId={}, amount={}, newBalance={}", 
                    orderNo, user.getId(), transaction.getAmount(), newBalance);
        }
    }
}
