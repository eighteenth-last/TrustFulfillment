package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.TfOrder;
import com.trustfulfillment.entity.TfOrderStage;
import com.trustfulfillment.service.SysUserService;
import com.trustfulfillment.service.TfOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private TfOrderService orderService;

    @Autowired
    private SysUserService userService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<?> createOrder(@RequestBody Map<String, Object> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            TfOrder order = new TfOrder();
            order.setBuyerId(userId);
            order.setTitle((String) params.get("title"));
            order.setDescription((String) params.get("description"));
            
            // 解析新增字段
            if (params.get("categoryId") != null) {
                order.setCategoryId(Integer.parseInt(params.get("categoryId").toString()));
            } else {
                order.setCategoryId(5); // 默认其他
            }
            order.setTechStack((String) params.get("techStack"));
            order.setFeatures((String) params.get("features"));
            
            // 处理文档链接 (JSON数组)
            @SuppressWarnings("unchecked")
            List<String> docUrlList = (List<String>) params.get("docUrls");
            if (docUrlList != null && !docUrlList.isEmpty()) {
                order.setDocUrls(String.join(",", docUrlList));
            }

            // 解析时间
            String startTimeStr = (String) params.get("startTime");
            String deliveryTimeStr = (String) params.get("deliveryTime");
            if (startTimeStr != null) {
                order.setStartTime(LocalDateTime.parse(startTimeStr + "T00:00:00"));
            }
            if (deliveryTimeStr != null) {
                order.setDeliveryTime(LocalDateTime.parse(deliveryTimeStr + "T00:00:00"));
            }

            // 解析阶段
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> stagesData = (List<Map<String, Object>>) params.get("stages");
            List<TfOrderStage> stages = stagesData.stream().map(s -> {
                TfOrderStage stage = new TfOrderStage();
                stage.setStageName((String) s.get("name"));
                stage.setAmount(new java.math.BigDecimal(s.get("amount").toString()));
                // 解析新增字段
                if (s.get("percent") != null) {
                    stage.setPercent(Integer.parseInt(s.get("percent").toString()));
                }
                stage.setMilestone((String) s.get("milestone"));
                stage.setRiskNote((String) s.get("riskNote"));
                return stage;
            }).collect(Collectors.toList());

            TfOrder created = orderService.createOrder(order, stages);
            return Result.success("订单创建成功", created);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public Result<?> getOrderList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) Integer status) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);

        // 判断用户角色：优先使用 isMerchant 字段判断是否是商家
        // 如果 isMerchant=1 且请求 status=0（任务大厅），则视为商家查询
        String effectiveRole = user.getRole();
        if (user.getIsMerchant() != null && user.getIsMerchant() == 1) {
            effectiveRole = "merchant";
        }

        IPage<TfOrder> orders = orderService.pageOrders(userId, effectiveRole, status, page, size);
        return Result.success(orders);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<?> getOrderDetail(@PathVariable("id") Long id) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        try {
            Map<String, Object> detail = orderService.getOrderDetail(id);
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 接单（商家）
     */
    @PostMapping("/{id}/accept")
    public Result<?> acceptOrder(@PathVariable("id") Long id) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            orderService.acceptOrder(id, userId);
            return Result.success("接单成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 托管资金（用户付款）
     */
    @PostMapping("/{id}/deposit")
    public Result<?> depositFunds(@PathVariable("id") Long id) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            orderService.depositFunds(id, userId);
            return Result.success("支付成功，任务已发布", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 支付下一阶段款项（用户）
     */
    @PostMapping("/{id}/pay-next-stage")
    public Result<?> payNextStage(@PathVariable("id") Long id) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            orderService.payNextStage(id, userId);
            return Result.success("支付成功，下一阶段已开始", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 开始阶段（商家）
     */
    @PostMapping("/{orderId}/stage/{stageId}/start")
    public Result<?> startStage(
            @PathVariable("orderId") Long orderId,
            @PathVariable("stageId") Long stageId) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            orderService.startStage(orderId, stageId, userId);
            return Result.success("阶段已开始", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 提交交付（商家）
     */
    @PostMapping("/{orderId}/stage/{stageId}/deliver")
    public Result<?> submitDelivery(
            @PathVariable("orderId") Long orderId,
            @PathVariable("stageId") Long stageId,
            @RequestBody Map<String, String> params) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            String evidenceUrl = params.get("evidenceUrl");
            String deliveryDesc = params.get("deliveryDesc");
            orderService.submitDelivery(orderId, stageId, evidenceUrl, deliveryDesc, userId);
            return Result.success("交付提交成功，等待验收", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验收确认（用户）
     */
    @PostMapping("/{orderId}/stage/{stageId}/accept")
    public Result<?> acceptDelivery(
            @PathVariable("orderId") Long orderId,
            @PathVariable("stageId") Long stageId) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            orderService.acceptDelivery(orderId, stageId, userId);
            return Result.success("验收通过", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 释放质保款（用户）
     */
    @PostMapping("/{orderId}/release-warranty")
    public Result<?> releaseWarranty(@PathVariable("orderId") Long orderId) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            orderService.releaseWarranty(orderId, userId);
            return Result.success("质保款已释放给服务商，订单完成", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 拒绝验收（用户）
     */
    @PostMapping("/{orderId}/stage/{stageId}/reject")
    public Result<?> rejectDelivery(
            @PathVariable("orderId") Long orderId,
            @PathVariable("stageId") Long stageId,
            @RequestBody Map<String, String> params) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            String reason = params.get("reason");
            orderService.rejectDelivery(orderId, stageId, userId, reason);
            return Result.success("已拒绝验收，请与服务商沟通修改", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取阶段详情
     */
    @GetMapping("/stage/{stageId}")
    public Result<?> getStageDetail(@PathVariable("stageId") Long stageId) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        try {
            TfOrderStage stage = orderService.getStageDetail(stageId);
            if (stage == null) {
                return Result.error("阶段不存在");
            }
            return Result.success(stage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable("id") Long id) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        try {
            orderService.cancelOrder(id, userId);
            return Result.success("订单已取消", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats")
    public Result<?> getUserStats() {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);

        // 判断用户角色：优先使用 isMerchant 字段判断是否是商家
        String effectiveRole = user.getRole();
        if (user.getIsMerchant() != null && user.getIsMerchant() == 1) {
            effectiveRole = "merchant";
        }

        Map<String, Object> stats = orderService.getUserStats(userId, effectiveRole);
        return Result.success(stats);
    }
}
