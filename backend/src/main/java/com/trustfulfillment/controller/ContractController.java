package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.TfContract;
import com.trustfulfillment.service.ContractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 合同控制器
 */
@RestController
@RequestMapping("/api/contract")
@CrossOrigin(origins = "*")
public class ContractController {

    private static final Logger log = LoggerFactory.getLogger(ContractController.class);

    @Autowired
    private ContractService contractService;

    /**
     * 创建或更新合同（商家操作）
     */
    @PostMapping("/create")
    public Result<?> createOrUpdateContract(@RequestBody Map<String, Object> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long merchantId = StpUtil.getLoginIdAsLong();
        Long orderId = Long.parseLong(params.get("orderId").toString());

        try {
            TfContract contract = contractService.createOrUpdateContract(orderId, params, merchantId);
            return Result.success("保存成功", contract);
        } catch (Exception e) {
            log.error("创建合同失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交合同（商家完成编辑）
     */
    @PostMapping("/{id}/submit")
    public Result<?> submitContract(@PathVariable("id") Long id) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long merchantId = StpUtil.getLoginIdAsLong();

        try {
            boolean success = contractService.submitContract(id, merchantId);
            if (success) {
                return Result.success("合同已提交，等待甲方签署", null);
            } else {
                return Result.error("提交失败");
            }
        } catch (Exception e) {
            log.error("提交合同失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 签署合同
     */
    @PostMapping("/{id}/sign")
    public Result<?> signContract(@PathVariable("id") Long id, @RequestBody Map<String, String> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        String signature = params.get("signature");

        try {
            boolean success = contractService.signContract(id, signature, userId);
            if (success) {
                // 检查合同状态
                TfContract contract = contractService.getContractDetail(id);
                if (contract.getStatus() == TfContract.STATUS_EFFECTIVE) {
                    return Result.success("合同已生效", contract);
                } else {
                    return Result.success("签署成功，等待对方签署", contract);
                }
            } else {
                return Result.error("签署失败");
            }
        } catch (Exception e) {
            log.error("签署合同失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取合同详情
     */
    @GetMapping("/{id}")
    public Result<?> getContractDetail(@PathVariable("id") Long id) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        TfContract contract = contractService.getContractDetail(id);
        if (contract == null) {
            return Result.error("合同不存在");
        }

        // 验证权限
        Long userId = StpUtil.getLoginIdAsLong();
        if (!contract.getPartyAId().equals(userId) && !contract.getPartyBId().equals(userId)) {
            return Result.error("无权查看此合同");
        }

        return Result.success(contract);
    }

    /**
     * 根据订单获取合同
     */
    @GetMapping("/order/{orderId}")
    public Result<?> getContractByOrder(@PathVariable("orderId") Long orderId) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        TfContract contract = contractService.getContractByOrder(orderId);
        return Result.success(contract);
    }

    /**
     * 获取当前用户的合同列表
     */
    @GetMapping("/list")
    public Result<?> getContractList() {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        java.util.List<TfContract> contracts = contractService.getContractListByUser(userId);
        return Result.success(contracts);
    }
}
