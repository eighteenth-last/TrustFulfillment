package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.*;
import com.trustfulfillment.mapper.TfDisputeMapper;
import com.trustfulfillment.mapper.TfDisputeMessageMapper;
import com.trustfulfillment.service.RiskService;
import com.trustfulfillment.service.SysUserService;
import com.trustfulfillment.service.TfOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dispute")
@CrossOrigin(origins = "*")
public class DisputeController {

    @Autowired
    private TfDisputeMapper disputeMapper;

    @Autowired
    private TfDisputeMessageMapper messageMapper;

    @Autowired
    private TfOrderService orderService;

    @Autowired
    private SysUserService userService;
    
    @Autowired
    private RiskService riskService;

    /**
     * 发起申诉
     */
    @PostMapping("/create")
    public Result<?> createDispute(@RequestBody Map<String, Object> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        Long orderId = Long.parseLong(params.get("orderId").toString());
        String reason = (String) params.get("reason");

        // 获取订单信息
        TfOrder order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 确定发起人角色
        String role;
        if (order.getBuyerId().equals(userId)) {
            role = "user";
        } else if (order.getSellerId() != null && order.getSellerId().equals(userId)) {
            role = "merchant";
        } else {
            return Result.error("无权对此订单发起申诉");
        }

        // 检查是否已有未处理的申诉
        LambdaQueryWrapper<TfDispute> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(TfDispute::getOrderId, orderId)
                .in(TfDispute::getStatus, 0, 1);
        if (disputeMapper.selectCount(existWrapper) > 0) {
            return Result.error("该订单已有进行中的申诉");
        }

        try {
            TfDispute dispute = new TfDispute();
            dispute.setOrderId(orderId);
            dispute.setInitiatorId(userId);
            dispute.setInitiatorRole(role);
            dispute.setReason(reason);
            dispute.setStatus(0); // 待处理

            @SuppressWarnings("unchecked")
            List<String> evidenceUrls = (List<String>) params.get("evidenceUrls");
            if (evidenceUrls != null && !evidenceUrls.isEmpty()) {
                dispute.setEvidenceUrls(String.join(",", evidenceUrls));
            }

            disputeMapper.insert(dispute);

            // 更新订单状态为纠纷中
            order.setStatus(6);
            orderService.updateById(order);
            
            // 风控检测：纠纷风险
            try {
                riskService.checkDisputeRisk(dispute.getId(), orderId, order.getTotalAmount());
            } catch (Exception e) {
                // 风控检测失败不影响纠纷创建
                e.printStackTrace();
            }

            return Result.success("申诉已提交，平台将尽快介入处理", dispute.getId());
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单的申诉详情
     */
    @GetMapping("/order/{orderId}")
    public Result<?> getDisputeByOrder(@PathVariable("orderId") Long orderId) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        LambdaQueryWrapper<TfDispute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfDispute::getOrderId, orderId)
                .orderByDesc(TfDispute::getCreateTime)
                .last("LIMIT 1");

        TfDispute dispute = disputeMapper.selectOne(wrapper);
        if (dispute == null) {
            return Result.success(null);
        }

        // 填充信息
        TfOrder order = orderService.getById(orderId);
        if (order != null) {
            dispute.setOrderTitle(order.getTitle());
        }

        SysUser initiator = userService.getById(dispute.getInitiatorId());
        if (initiator != null) {
            dispute.setInitiatorName(initiator.getNickname());
        }

        // 获取留言列表
        LambdaQueryWrapper<TfDisputeMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(TfDisputeMessage::getDisputeId, dispute.getId())
                .orderByAsc(TfDisputeMessage::getCreateTime);
        List<TfDisputeMessage> messages = messageMapper.selectList(msgWrapper);

        // 填充留言者信息
        for (TfDisputeMessage msg : messages) {
            SysUser msgUser = userService.getById(msg.getUserId());
            if (msgUser != null) {
                msg.setUserName(msgUser.getNickname());
                msg.setUserAvatar(msgUser.getAvatar());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dispute", dispute);
        result.put("messages", messages);

        return Result.success(result);
    }

    /**
     * 发送留言
     */
    @PostMapping("/{disputeId}/message")
    public Result<?> sendMessage(
            @PathVariable("disputeId") Long disputeId,
            @RequestBody Map<String, Object> params) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);

        TfDispute dispute = disputeMapper.selectById(disputeId);
        if (dispute == null) {
            return Result.error("申诉不存在");
        }

        // 检查是否有权限留言（订单双方或管理员）
        TfOrder order = orderService.getById(dispute.getOrderId());
        boolean isBuyer = order.getBuyerId().equals(userId);
        boolean isSeller = order.getSellerId() != null && order.getSellerId().equals(userId);
        boolean isAdmin = "admin".equals(user.getRole());

        if (!isBuyer && !isSeller && !isAdmin) {
            return Result.error("无权在此申诉中留言");
        }

        try {
            TfDisputeMessage message = new TfDisputeMessage();
            message.setDisputeId(disputeId);
            message.setUserId(userId);
            message.setContent((String) params.get("content"));

            // 确定角色
            if (isAdmin) {
                message.setRole("admin");
                // 管理员留言后，状态改为处理中
                if (dispute.getStatus() == 0) {
                    dispute.setStatus(1);
                    dispute.setAdminId(userId);
                    disputeMapper.updateById(dispute);
                }
            } else if (isBuyer) {
                message.setRole("user");
            } else {
                message.setRole("merchant");
            }

            @SuppressWarnings("unchecked")
            List<String> attachments = (List<String>) params.get("attachments");
            if (attachments != null && !attachments.isEmpty()) {
                message.setAttachments(String.join(",", attachments));
            }

            messageMapper.insert(message);

            return Result.success("留言发送成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户的申诉列表
     */
    @GetMapping("/list")
    public Result<?> getDisputeList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        LambdaQueryWrapper<TfDispute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfDispute::getInitiatorId, userId)
                .orderByDesc(TfDispute::getCreateTime);

        IPage<TfDispute> disputes = disputeMapper.selectPage(new Page<>(page, size), wrapper);

        // 填充订单信息
        for (TfDispute dispute : disputes.getRecords()) {
            TfOrder order = orderService.getById(dispute.getOrderId());
            if (order != null) {
                dispute.setOrderTitle(order.getTitle());
            }
        }

        return Result.success(disputes);
    }

    /**
     * 撤销申诉
     */
    @PostMapping("/{disputeId}/cancel")
    public Result<?> cancelDispute(@PathVariable("disputeId") Long disputeId) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();

        TfDispute dispute = disputeMapper.selectById(disputeId);
        if (dispute == null) {
            return Result.error("申诉不存在");
        }

        if (!dispute.getInitiatorId().equals(userId)) {
            return Result.error("只有申诉发起人可以撤销");
        }

        if (dispute.getStatus() >= 2) {
            return Result.error("申诉已裁决，无法撤销");
        }

        try {
            dispute.setStatus(3); // 已撤销
            disputeMapper.updateById(dispute);

            // 恢复订单状态为进行中
            TfOrder order = orderService.getById(dispute.getOrderId());
            if (order != null && order.getStatus() == 6) {
                order.setStatus(2);
                orderService.updateById(order);
            }

            return Result.success("申诉已撤销", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
