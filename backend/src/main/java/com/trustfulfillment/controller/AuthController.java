package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.dto.*;
import com.trustfulfillment.entity.SysSmsCode;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.SysUserOauth;
import com.trustfulfillment.service.SmsService;
import com.trustfulfillment.service.SysUserService;
import com.trustfulfillment.service.WechatService;
import com.trustfulfillment.entity.SysCategory;
import com.trustfulfillment.mapper.SysCategoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 支持: 手机号验证码登录、手机号密码登录、微信一键登录
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private SysUserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private SysCategoryMapper categoryMapper;

    /**
     * 传统登录接口 (兼容旧版本，支持手机号+密码)
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody java.util.Map<String, String> params, HttpServletRequest request) {
        String username = params.get("username");
        String phone = params.get("phone");
        String password = params.get("password");

        // 优先使用 phone 字段，如果没有则使用 username
        String loginPhone = phone != null && !phone.isEmpty() ? phone : username;

        if (loginPhone == null || loginPhone.isEmpty()) {
            return Result.error("请输入手机号/账号");
        }
        if (password == null || password.isEmpty()) {
            return Result.error("请输入密码");
        }

        String ip = getClientIp(request);

        try {
            LoginResultDTO result = userService.loginByPhonePassword(
                    loginPhone, password, ip, null, "web");
            return Result.success(result);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 注册接口
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody java.util.Map<String, String> params, HttpServletRequest request) {
        String phone = params.get("phone");
        String password = params.get("password");
        String nickname = params.get("nickname");
        String role = params.get("role");

        if (phone == null || phone.isEmpty()) {
            return Result.error("请输入手机号");
        }
        if (password == null || password.isEmpty()) {
            return Result.error("请输入密码");
        }

        // 检查手机号是否已注册
        if (userService.getByPhone(phone) != null) {
            return Result.error("该手机号已注册");
        }

        try {
            // 创建用户
            SysUser user = new SysUser();
            user.setPhone(phone);
            user.setPassword(cn.hutool.crypto.digest.BCrypt.hashpw(password));
            user.setNickname(nickname != null ? nickname : "用户" + phone.substring(7));
            user.setRole(role != null ? role : "user");
            user.setBalance(java.math.BigDecimal.ZERO);
            user.setFrozenAmount(java.math.BigDecimal.ZERO);
            user.setCreditScore(600);
            user.setStatus(1);
            user.setVerified(0);
            user.setRegisterSource("web");

            userService.save(user);

            return Result.success("注册成功", null);
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发送短信验证码
     */
    @PostMapping("/sms/send")
    public Result<?> sendSmsCode(@Valid @RequestBody SendSmsCodeDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);

        try {
            boolean success = smsService.sendVerifyCode(dto.getPhone(), dto.getType(), ip);
            if (success) {
                return Result.success("验证码发送成功", null);
            } else {
                return Result.error("验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 手机号验证码登录/注册 (一键登录)
     */
    @PostMapping("/login/phone")
    public Result<?> loginByPhoneCode(@Valid @RequestBody PhoneLoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);

        // 验证验证码
        if (!smsService.verifyCode(dto.getPhone(), dto.getCode(), SysSmsCode.TYPE_LOGIN)) {
            return Result.error("验证码错误或已过期");
        }

        try {
            LoginResultDTO result = userService.loginByPhoneCode(
                    dto.getPhone(), ip, dto.getDevice(), dto.getPlatform());
            return Result.success(result);
        } catch (Exception e) {
            log.error("手机号登录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 手机号密码登录
     */
    @PostMapping("/login/password")
    public Result<?> loginByPassword(@Valid @RequestBody PhonePasswordLoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);

        try {
            LoginResultDTO result = userService.loginByPhonePassword(
                    dto.getPhone(), dto.getPassword(), ip, dto.getDevice(), dto.getPlatform());
            return Result.success(result);
        } catch (Exception e) {
            log.error("密码登录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 微信小程序登录
     */
    @PostMapping("/login/wechat")
    public Result<?> loginByWechat(@Valid @RequestBody WechatLoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);

        try {
            // 调用微信接口获取openid
            WechatService.Code2SessionResult sessionResult = wechatService.code2Session(dto.getCode());
            if (!sessionResult.isSuccess()) {
                return Result.error("微信登录失败: " + sessionResult.getErrmsg());
            }

            // 获取或创建OAuth记录
            SysUserOauth oauth = wechatService.getOrCreateOauth(
                    sessionResult.getOpenid(),
                    sessionResult.getUnionid(),
                    "wechat_mp",
                    dto.getUserInfo(),
                    sessionResult.getSessionKey());

            // 执行登录
            LoginResultDTO result = userService.loginByWechat(oauth, ip, dto.getDevice(), dto.getPlatform());
            return Result.success(result);
        } catch (Exception e) {
            log.error("微信登录失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 绑定手机号 (微信用户首次登录需要绑定)
     */
    @PostMapping("/bindPhone")
    public Result<?> bindPhone(@Valid @RequestBody BindPhoneDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);

        // 验证验证码
        if (!smsService.verifyCode(dto.getPhone(), dto.getCode(), SysSmsCode.TYPE_BIND_PHONE)) {
            return Result.error("验证码错误或已过期");
        }

        try {
            LoginResultDTO result = userService.bindPhone(
                    dto.getPhone(), dto.getOpenid(), dto.getMerge(), ip, null, "mp");
            return Result.success(result);
        } catch (Exception e) {
            log.error("绑定手机号失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 设置/修改密码
     */
    @PostMapping("/setPassword")
    public Result<?> setPassword(@Valid @RequestBody SetPasswordDTO dto) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return Result.error("两次密码输入不一致");
        }

        try {
            Long userId = StpUtil.getLoginIdAsLong();
            userService.setPassword(userId, dto.getOldPassword(), dto.getNewPassword());
            return Result.success("密码设置成功", null);
        } catch (Exception e) {
            log.error("设置密码失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<?> getUserInfo() {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);

        if (user == null) {
            return Result.error("用户不存在");
        }

        return Result.success(userService.buildUserInfo(user));
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        StpUtil.logout();
        return Result.success("退出成功", null);
    }

    /**
     * 检查登录状态
     */
    @GetMapping("/check")
    public Result<?> checkLogin() {
        return Result.success(StpUtil.isLogin());
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<?> updateUserInfo(@RequestBody java.util.Map<String, String> params) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.getById(userId);

        if (user == null) {
            return Result.error("用户不存在");
        }

        // 更新允许修改的字段
        String avatar = params.get("avatar");
        String nickname = params.get("nickname");
        String email = params.get("email");
        String realName = params.get("realName");

        if (avatar != null) {
            user.setAvatar(avatar);
        }
        if (nickname != null && !nickname.isEmpty()) {
            user.setNickname(nickname);
        }
        if (email != null) {
            user.setEmail(email);
        }
        if (realName != null && !realName.isEmpty()) {
            user.setRealName(realName);
        }

        boolean success = userService.updateById(user);
        if (success) {
            return Result.success("更新成功", userService.buildUserInfo(user));
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 获取业务分类列表（树形结构）
     */
    @GetMapping("/categories")
    public Result<?> getBusinessCategories() {
        // 查询所有启用的分类
        LambdaQueryWrapper<SysCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCategory::getStatus, 1)
                .orderByAsc(SysCategory::getSortOrder);
        java.util.List<SysCategory> allCategories = categoryMapper.selectList(wrapper);

        // 构建树形结构
        java.util.List<SysCategory> tree = buildCategoryTree(allCategories, 0);
        return Result.success(tree);
    }

    /**
     * 递归构建分类树
     */
    private java.util.List<SysCategory> buildCategoryTree(java.util.List<SysCategory> allCategories, Integer parentId) {
        java.util.List<SysCategory> result = new java.util.ArrayList<>();
        for (SysCategory category : allCategories) {
            if (category.getParentId().equals(parentId)) {
                java.util.List<SysCategory> children = buildCategoryTree(allCategories, category.getId());
                if (!children.isEmpty()) {
                    category.setChildren(children);
                }
                result.add(category);
            }
        }
        return result;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // IPv6 本地回环地址转换为 IPv4 格式
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
