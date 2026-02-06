package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.TfEvidence;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.mapper.TfOrderMapper;
import com.trustfulfillment.service.EvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存证控制器
 */
@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private TfOrderMapper orderMapper;

    /**
     * 获取订单的所有存证记录
     */
    @GetMapping("/order/{orderId}")
    public Result<Map<String, Object>> getOrderEvidence(@PathVariable("orderId") Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        // 验证用户权限
        TfOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            return Result.error("您无权查看此订单的存证记录");
        }
        
        List<TfEvidence> evidences = evidenceService.getOrderEvidence(orderId);
        
        // 按类型分组统计
        Map<Integer, Long> typeCount = new HashMap<>();
        for (TfEvidence e : evidences) {
            typeCount.merge(e.getType(), 1L, Long::sum);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", evidences);
        result.put("total", evidences.size());
        result.put("typeCount", typeCount);
        
        return Result.success(result);
    }

    /**
     * 获取存证详情
     */
    @GetMapping("/{id}")
    public Result<TfEvidence> getEvidenceDetail(@PathVariable("id") Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        TfEvidence evidence = evidenceService.getById(id);
        if (evidence == null) {
            return Result.error("存证记录不存在");
        }
        
        // 验证用户权限
        TfOrder order = orderMapper.selectById(evidence.getOrderId());
        if (order == null) {
            return Result.error("关联订单不存在");
        }
        
        if (!userId.equals(order.getBuyerId()) && !userId.equals(order.getSellerId())) {
            return Result.error("您无权查看此存证记录");
        }
        
        return Result.success(evidence);
    }

    /**
     * 获取存证类型名称
     */
    @GetMapping("/types")
    public Result<Map<Integer, String>> getEvidenceTypes() {
        Map<Integer, String> types = new HashMap<>();
        types.put(1, "合同签署");
        types.put(2, "资金托管");
        types.put(3, "交付提交");
        types.put(4, "验收确认");
        types.put(5, "纠纷证据");
        return Result.success(types);
    }
}
