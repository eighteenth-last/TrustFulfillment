package com.trustfulfillment.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.trustfulfillment.dto.LoginResultDTO;
import com.trustfulfillment.entity.SysLoginLog;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.SysUserOauth;
import com.trustfulfillment.mapper.SysLoginLogMapper;
import com.trustfulfillment.mapper.SysUserMapper;
import com.trustfulfillment.mapper.SysUserOauthMapper;
import com.trustfulfillment.service.SysUserService;
import com.trustfulfillment.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserOauthMapper oauthMapper;

    @Autowired
    private SysLoginLogMapper loginLogMapper;

    @Autowired
    private WechatService wechatService;

    @Override
    public SysUser getByPhone(String phone) {
        return getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
    }

    @Override
    @Transactional
    public LoginResultDTO loginByPhoneCode(String phone, String ip, String device, String platform) {
        SysUser user = getByPhone(phone);
        boolean isNewUser = false;

        if (user == null) {
            // 新用户，自动注册
            user = new SysUser();
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(7)); // 默认昵称
            user.setRole("user");
            user.setBalance(BigDecimal.ZERO);
            user.setFrozenAmount(BigDecimal.ZERO);
            user.setCreditScore(600);
            user.setStatus(1);
            user.setVerified(0);
            user.setRegisterSource("phone");
            save(user);
            isNewUser = true;
            log.info("新用户注册成功，手机号: {}", phone);
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 登录
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 更新登录信息
        updateLoginInfo(user.getId(), ip);

        // 记录登录日志
        saveLoginLog(user.getId(), SysLoginLog.TYPE_PHONE_CODE, phone, null, ip, device, platform, true, null);

        return LoginResultDTO.builder()
                .token(token)
                .isNewUser(isNewUser)
                .needBindPhone(false)
                .userInfo(buildUserInfo(user))
                .build();
    }

    @Override
    @Transactional
    public LoginResultDTO loginByPhonePassword(String phone, String password, String ip, String device, String platform) {
        SysUser user = getByPhone(phone);

        if (user == null) {
            saveLoginLog(null, SysLoginLog.TYPE_PHONE_PWD, phone, null, ip, device, platform, false, "用户不存在");
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == 0) {
            saveLoginLog(user.getId(), SysLoginLog.TYPE_PHONE_PWD, phone, null, ip, device, platform, false, "账号已被禁用");
            throw new RuntimeException("账号已被禁用");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            saveLoginLog(user.getId(), SysLoginLog.TYPE_PHONE_PWD, phone, null, ip, device, platform, false, "未设置密码");
            throw new RuntimeException("您尚未设置密码，请使用验证码登录");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            saveLoginLog(user.getId(), SysLoginLog.TYPE_PHONE_PWD, phone, null, ip, device, platform, false, "密码错误");
            throw new RuntimeException("密码错误");
        }

        // 登录
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 更新登录信息
        updateLoginInfo(user.getId(), ip);

        // 记录登录日志
        saveLoginLog(user.getId(), SysLoginLog.TYPE_PHONE_PWD, phone, null, ip, device, platform, true, null);

        return LoginResultDTO.builder()
                .token(token)
                .isNewUser(false)
                .needBindPhone(false)
                .userInfo(buildUserInfo(user))
                .build();
    }

    @Override
    @Transactional
    public LoginResultDTO loginByWechat(SysUserOauth oauth, String ip, String device, String platform) {
        // 检查是否已绑定用户
        if (oauth.getUserId() == null) {
            // 未绑定，需要绑定手机号
            return LoginResultDTO.builder()
                    .needBindPhone(true)
                    .openid(oauth.getOpenid())
                    .build();
        }

        // 已绑定，查询用户
        SysUser user = getById(oauth.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getStatus() == 0) {
            saveLoginLog(user.getId(), SysLoginLog.TYPE_WECHAT, user.getPhone(), oauth.getOpenid(),
                    ip, device, platform, false, "账号已被禁用");
            throw new RuntimeException("账号已被禁用");
        }

        // 登录
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 更新登录信息
        updateLoginInfo(user.getId(), ip);

        // 更新用户头像昵称（如果微信有新数据）
        if (oauth.getNickname() != null || oauth.getAvatar() != null) {
            LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysUser::getId, user.getId());
            if (oauth.getNickname() != null && (user.getNickname() == null || user.getNickname().startsWith("用户"))) {
                updateWrapper.set(SysUser::getNickname, oauth.getNickname());
            }
            if (oauth.getAvatar() != null && user.getAvatar() == null) {
                updateWrapper.set(SysUser::getAvatar, oauth.getAvatar());
            }
            if (oauth.getGender() != null && (user.getGender() == null || user.getGender() == 0)) {
                updateWrapper.set(SysUser::getGender, oauth.getGender());
            }
            update(updateWrapper);
            user = getById(user.getId());
        }

        // 记录登录日志
        saveLoginLog(user.getId(), SysLoginLog.TYPE_WECHAT, user.getPhone(), oauth.getOpenid(),
                ip, device, platform, true, null);

        return LoginResultDTO.builder()
                .token(token)
                .isNewUser(false)
                .needBindPhone(false)
                .userInfo(buildUserInfo(user))
                .build();
    }

    @Override
    @Transactional
    public LoginResultDTO bindPhone(String phone, String openid, boolean merge, String ip, String device, String platform) {
        // 查询OAuth记录
        SysUserOauth oauth = oauthMapper.selectOne(
                new LambdaQueryWrapper<SysUserOauth>()
                        .eq(SysUserOauth::getOpenid, openid)
        );

        if (oauth == null) {
            throw new RuntimeException("微信授权信息不存在，请重新登录");
        }

        // 检查手机号是否已被使用
        SysUser existingUser = getByPhone(phone);
        SysUser user;
        boolean isNewUser = false;

        if (existingUser != null) {
            if (merge) {
                // 合并账号：将微信绑定到已有账号
                user = existingUser;
            } else {
                throw new RuntimeException("该手机号已被注册，是否绑定到该账号？");
            }
        } else {
            // 创建新用户
            user = new SysUser();
            user.setPhone(phone);
            user.setNickname(oauth.getNickname() != null ? oauth.getNickname() : "用户" + phone.substring(7));
            user.setAvatar(oauth.getAvatar());
            user.setGender(oauth.getGender());
            user.setRole("user");
            user.setBalance(BigDecimal.ZERO);
            user.setFrozenAmount(BigDecimal.ZERO);
            user.setCreditScore(600);
            user.setStatus(1);
            user.setVerified(0);
            user.setRegisterSource("wechat");
            save(user);
            isNewUser = true;
        }

        // 绑定OAuth到用户
        wechatService.bindUser(oauth.getId(), user.getId());

        // 登录
        StpUtil.login(user.getId());
        String token = StpUtil.getTokenValue();

        // 更新登录信息
        updateLoginInfo(user.getId(), ip);

        // 记录登录日志
        saveLoginLog(user.getId(), SysLoginLog.TYPE_WECHAT, phone, openid, ip, device, platform, true, null);

        return LoginResultDTO.builder()
                .token(token)
                .isNewUser(isNewUser)
                .needBindPhone(false)
                .userInfo(buildUserInfo(user))
                .build();
    }

    @Override
    @Transactional
    public void setPassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 如果已有密码，需要验证原密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            if (oldPassword == null || oldPassword.isEmpty()) {
                throw new RuntimeException("请输入原密码");
            }
            if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
                throw new RuntimeException("原密码错误");
            }
        }

        // 设置新密码
        update(new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, userId)
                .set(SysUser::getPassword, BCrypt.hashpw(newPassword))
        );
    }

    @Override
    public void updateLoginInfo(Long userId, String ip) {
        update(new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, userId)
                .set(SysUser::getLastLoginTime, LocalDateTime.now())
                .set(SysUser::getLastLoginIp, ip)
        );
    }

    @Override
    public LoginResultDTO.UserInfo buildUserInfo(SysUser user) {
        return LoginResultDTO.UserInfo.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .gender(user.getGender())
                .role(user.getRole())
                .isMerchant(user.getIsMerchant() != null ? user.getIsMerchant() : 0)
                .merchantId(user.getMerchantId())
                .balance(user.getBalance())
                .frozenAmount(user.getFrozenAmount())
                .creditScore(user.getCreditScore())
                .verified(user.getVerified())
                .build();
    }

    private void saveLoginLog(Long userId, String loginType, String phone, String openid,
                              String ip, String device, String platform, boolean success, String failReason) {
        SysLoginLog log = new SysLoginLog();
        log.setUserId(userId);
        log.setLoginType(loginType);
        log.setPhone(phone);
        log.setOpenid(openid);
        log.setIp(ip);
        log.setDevice(device);
        log.setPlatform(platform);
        log.setStatus(success ? 1 : 0);
        log.setFailReason(failReason);
        loginLogMapper.insert(log);
    }

    @Override
    @Transactional
    public boolean updateBalance(Long userId, BigDecimal amount, boolean isAdd) {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getId, userId);

        if (isAdd) {
            wrapper.setSql("balance = balance + " + amount);
        } else {
            wrapper.setSql("balance = balance - " + amount);
            wrapper.ge(SysUser::getBalance, amount);
        }

        return update(wrapper);
    }

    @Override
    @Transactional
    public boolean updateFrozenAmount(Long userId, BigDecimal amount, boolean isFreeze) {
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysUser::getId, userId);

        if (isFreeze) {
            wrapper.setSql("balance = balance - " + amount + ", frozen_amount = frozen_amount + " + amount);
            wrapper.ge(SysUser::getBalance, amount);
        } else {
            wrapper.setSql("frozen_amount = frozen_amount - " + amount);
            wrapper.ge(SysUser::getFrozenAmount, amount);
        }

        return update(wrapper);
    }

    @Override
    public Long countByRole(String role) {
        return count(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, role)
                .eq(SysUser::getStatus, 1));
    }

    @Override
    public Long countNewUsersToday() {
        return count(new LambdaQueryWrapper<SysUser>()
                .ge(SysUser::getCreateTime, LocalDate.now().atStartOfDay()));
    }

    @Override
    public List<SysUser> listPendingMerchants(int limit) {
        return list(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, "merchant")
                .eq(SysUser::getVerified, 0)
                .orderByDesc(SysUser::getCreateTime)
                .last("LIMIT " + limit));
    }

    @Override
    public IPage<SysUser> pageMerchants(int page, int size, Integer status, String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRole, "merchant");
        if (status != null) {
            wrapper.eq(SysUser::getVerified, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public IPage<SysUser> pageUsers(int page, int size, String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysUser::getRole, "user", "merchant");
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }
}
