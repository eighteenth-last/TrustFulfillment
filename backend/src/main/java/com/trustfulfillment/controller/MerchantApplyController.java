package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.TfMerchant;
import com.trustfulfillment.entity.TfMerchantApply;
import com.trustfulfillment.mapper.TfMerchantApplyMapper;
import com.trustfulfillment.mapper.TfMerchantMapper;
import com.trustfulfillment.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 商户申请控制器（用户端）
 */
@RestController
@RequestMapping("/api/merchant")
public class MerchantApplyController {

    @Autowired
    private TfMerchantApplyMapper applyMapper;

    @Autowired
    private TfMerchantMapper merchantMapper;

    @Autowired
    private SysUserService userService;

    /**
     * 获取当前用户的商户状态
     */
    @GetMapping("/status")
    public Result<?> getMerchantStatus() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("isMerchant", user.getIsMerchant() != null && user.getIsMerchant() == 1);

        // 如果是商户，返回商户信息
        if (user.getIsMerchant() != null && user.getIsMerchant() == 1 && user.getMerchantId() != null) {
            TfMerchant merchant = merchantMapper.selectById(user.getMerchantId());
            if (merchant != null) {
                result.put("merchant", merchant);
            }
        }

        // 检查是否有待审核的申请
        TfMerchantApply pendingApply = applyMapper.selectOne(
                new LambdaQueryWrapper<TfMerchantApply>()
                        .eq(TfMerchantApply::getUserId, userId)
                        .eq(TfMerchantApply::getStatus, 0)
                        .orderByDesc(TfMerchantApply::getCreateTime)
                        .last("LIMIT 1"));
        
        if (pendingApply != null) {
            result.put("hasPendingApply", true);
            result.put("pendingApply", pendingApply);
        } else {
            result.put("hasPendingApply", false);
        }

        return Result.success(result);
    }

    /**
     * 提交商户申请
     */
    @PostMapping("/apply")
    public Result<?> submitApply(@RequestBody TfMerchantApply apply) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);

        // 检查是否已经是商户
        if (user.getIsMerchant() != null && user.getIsMerchant() == 1) {
            return Result.error("您已经是商户，无需重复申请");
        }

        // 检查是否有待审核的申请
        Long pendingCount = applyMapper.selectCount(
                new LambdaQueryWrapper<TfMerchantApply>()
                        .eq(TfMerchantApply::getUserId, userId)
                        .eq(TfMerchantApply::getStatus, 0));
        if (pendingCount > 0) {
            return Result.error("您已有待审核的申请，请耐心等待");
        }

        // 生成申请编号
        String applyNo = "MA" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));

        apply.setApplyNo(applyNo);
        apply.setUserId(userId);
        apply.setStatus(0);
        apply.setContactPhone(apply.getContactPhone() != null ? apply.getContactPhone() : user.getPhone());

        applyMapper.insert(apply);

        return Result.success("申请提交成功，请等待审核", apply.getApplyNo());
    }

    /**
     * 获取申请历史
     */
    @GetMapping("/apply/history")
    public Result<?> getApplyHistory() {
        Long userId = StpUtil.getLoginIdAsLong();

        var list = applyMapper.selectList(
                new LambdaQueryWrapper<TfMerchantApply>()
                        .eq(TfMerchantApply::getUserId, userId)
                        .orderByDesc(TfMerchantApply::getCreateTime));

        return Result.success(list);
    }

    /**
     * 获取商户类型配置（提成比例等）
     */
    @GetMapping("/type-config")
    public Result<?> getMerchantTypeConfig() {
        Map<String, Object> config = new HashMap<>();

        // 个体工商户配置
        Map<String, Object> individual = new HashMap<>();
        individual.put("type", 1);
        individual.put("name", "个体工商户");
        individual.put("commissionRate", 8.0);  // 8%提成
        individual.put("description", "适合个人或小规模经营者");
        individual.put("requirements", new String[]{"身份证正反面", "营业执照（可选）"});

        // 企业/组织配置
        Map<String, Object> enterprise = new HashMap<>();
        enterprise.put("type", 2);
        enterprise.put("name", "企业/组织");
        enterprise.put("commissionRate", 5.0);  // 5%提成
        enterprise.put("description", "适合公司、团队或组织");
        enterprise.put("requirements", new String[]{"身份证正反面", "营业执照", "组织机构代码证（如有）"});

        config.put("individual", individual);
        config.put("enterprise", enterprise);

        return Result.success(config);
    }
}
