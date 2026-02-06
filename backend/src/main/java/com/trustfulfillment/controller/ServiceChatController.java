package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.TfServiceMessage;
import com.trustfulfillment.mapper.TfServiceMessageMapper;
import com.trustfulfillment.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客服聊天控制器
 * 用于用户与平台管理员之间的客服咨询
 */
@RestController
@RequestMapping("/api/service-chat")
public class ServiceChatController {

    @Autowired
    private TfServiceMessageMapper messageMapper;

    @Autowired
    private SysUserService userService;

    /**
     * 获取客服消息历史
     */
    @GetMapping("/history")
    public Result<Map<String, Object>> getMessageHistory() {
        Long userId = StpUtil.getLoginIdAsLong();
        
        LambdaQueryWrapper<TfServiceMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfServiceMessage::getUserId, userId)
               .orderByAsc(TfServiceMessage::getCreateTime);
        
        List<TfServiceMessage> messages = messageMapper.selectList(wrapper);
        
        // 填充发送者信息
        for (TfServiceMessage msg : messages) {
            if (msg.getSenderRole() == 1) {
                // 用户发送
                SysUser user = userService.getById(msg.getSenderId());
                if (user != null) {
                    msg.setSenderName(user.getNickname() != null ? user.getNickname() : user.getPhone());
                    msg.setSenderAvatar(user.getAvatar());
                }
            } else {
                // 管理员发送
                msg.setSenderName("平台客服");
                msg.setSenderAvatar(null);
            }
        }
        
        // 统计未读数量
        LambdaQueryWrapper<TfServiceMessage> unreadWrapper = new LambdaQueryWrapper<>();
        unreadWrapper.eq(TfServiceMessage::getUserId, userId)
                     .eq(TfServiceMessage::getSenderRole, 2) // 管理员发的消息
                     .eq(TfServiceMessage::getIsRead, 0);
        Long unreadCount = messageMapper.selectCount(unreadWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messages);
        result.put("unreadCount", unreadCount);
        result.put("currentUserId", userId);
        
        return Result.success(result);
    }

    /**
     * 发送消息给客服
     */
    @PostMapping("/send")
    public Result<TfServiceMessage> sendMessage(@RequestBody Map<String, Object> params) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        String content = (String) params.get("content");
        Integer type = params.get("type") != null ? Integer.parseInt(params.get("type").toString()) : 1;
        
        if (content == null || content.trim().isEmpty()) {
            return Result.error("消息内容不能为空");
        }
        
        TfServiceMessage message = new TfServiceMessage();
        message.setUserId(userId);
        message.setSenderId(userId);
        message.setSenderRole(1); // 用户
        message.setContent(content);
        message.setType(type);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        
        messageMapper.insert(message);
        
        // 填充发送者信息
        SysUser user = userService.getById(userId);
        if (user != null) {
            message.setSenderName(user.getNickname() != null ? user.getNickname() : user.getPhone());
            message.setSenderAvatar(user.getAvatar());
        }
        
        return Result.success(message);
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/read")
    public Result<Integer> markAsRead() {
        Long userId = StpUtil.getLoginIdAsLong();
        
        LambdaUpdateWrapper<TfServiceMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TfServiceMessage::getUserId, userId)
               .eq(TfServiceMessage::getSenderRole, 2) // 管理员发的消息
               .eq(TfServiceMessage::getIsRead, 0)
               .set(TfServiceMessage::getIsRead, 1);
        
        int count = messageMapper.update(null, wrapper);
        return Result.success(count);
    }

    // ========== 管理员接口 ==========

    /**
     * 获取所有用户的咨询列表（管理员）
     */
    @GetMapping("/admin/list")
    public Result<List<Map<String, Object>>> getConsultList() {
        // TODO: 验证管理员权限
        
        // 获取所有有消息的用户
        List<Map<String, Object>> list = messageMapper.selectMaps(
            new LambdaQueryWrapper<TfServiceMessage>()
                .select(TfServiceMessage::getUserId)
                .groupBy(TfServiceMessage::getUserId)
        );
        
        // 补充用户信息和最后消息
        for (Map<String, Object> item : list) {
            Long userId = Long.parseLong(item.get("userId").toString());
            SysUser user = userService.getById(userId);
            if (user != null) {
                item.put("nickname", user.getNickname() != null ? user.getNickname() : user.getPhone());
                item.put("avatar", user.getAvatar());
            }
            
            // 获取最后一条消息
            LambdaQueryWrapper<TfServiceMessage> lastWrapper = new LambdaQueryWrapper<>();
            lastWrapper.eq(TfServiceMessage::getUserId, userId)
                       .orderByDesc(TfServiceMessage::getCreateTime)
                       .last("LIMIT 1");
            TfServiceMessage lastMsg = messageMapper.selectOne(lastWrapper);
            if (lastMsg != null) {
                item.put("lastMessage", lastMsg.getContent());
                item.put("lastTime", lastMsg.getCreateTime());
            }
            
            // 统计管理员未读数量（用户发的消息）
            LambdaQueryWrapper<TfServiceMessage> unreadWrapper = new LambdaQueryWrapper<>();
            unreadWrapper.eq(TfServiceMessage::getUserId, userId)
                         .eq(TfServiceMessage::getSenderRole, 1) // 用户发的消息
                         .eq(TfServiceMessage::getIsRead, 0);
            Long unreadCount = messageMapper.selectCount(unreadWrapper);
            item.put("unreadCount", unreadCount);
        }
        
        return Result.success(list);
    }

    /**
     * 获取与指定用户的聊天历史（管理员）
     */
    @GetMapping("/admin/history/{userId}")
    public Result<Map<String, Object>> getAdminMessageHistory(@PathVariable("userId") Long userId) {
        // TODO: 验证管理员权限
        
        LambdaQueryWrapper<TfServiceMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TfServiceMessage::getUserId, userId)
               .orderByAsc(TfServiceMessage::getCreateTime);
        
        List<TfServiceMessage> messages = messageMapper.selectList(wrapper);
        
        // 填充发送者信息
        SysUser user = userService.getById(userId);
        for (TfServiceMessage msg : messages) {
            if (msg.getSenderRole() == 1) {
                // 用户发送
                if (user != null) {
                    msg.setSenderName(user.getNickname() != null ? user.getNickname() : user.getPhone());
                    msg.setSenderAvatar(user.getAvatar());
                }
            } else {
                // 管理员发送
                msg.setSenderName("平台客服");
                msg.setSenderAvatar(null);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messages);
        result.put("userId", userId);
        if (user != null) {
            result.put("userName", user.getNickname() != null ? user.getNickname() : user.getPhone());
        }
        
        return Result.success(result);
    }

    /**
     * 管理员回复消息
     */
    @PostMapping("/admin/reply")
    public Result<TfServiceMessage> adminReply(@RequestBody Map<String, Object> params) {
        // TODO: 验证管理员权限
        Long adminId = StpUtil.getLoginIdAsLong();
        
        Long userId = Long.parseLong(params.get("userId").toString());
        String content = (String) params.get("content");
        Integer type = params.get("type") != null ? Integer.parseInt(params.get("type").toString()) : 1;
        
        if (content == null || content.trim().isEmpty()) {
            return Result.error("消息内容不能为空");
        }
        
        TfServiceMessage message = new TfServiceMessage();
        message.setUserId(userId);
        message.setSenderId(adminId);
        message.setSenderRole(2); // 管理员
        message.setContent(content);
        message.setType(type);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        
        messageMapper.insert(message);
        
        message.setSenderName("平台客服");
        
        return Result.success(message);
    }

    /**
     * 管理员标记消息已读
     */
    @PostMapping("/admin/read/{userId}")
    public Result<Integer> adminMarkAsRead(@PathVariable("userId") Long userId) {
        // TODO: 验证管理员权限
        
        LambdaUpdateWrapper<TfServiceMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TfServiceMessage::getUserId, userId)
               .eq(TfServiceMessage::getSenderRole, 1) // 用户发的消息
               .eq(TfServiceMessage::getIsRead, 0)
               .set(TfServiceMessage::getIsRead, 1);
        
        int count = messageMapper.update(null, wrapper);
        return Result.success(count);
    }
}
