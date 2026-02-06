package com.trustfulfillment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.trustfulfillment.dto.LoginResultDTO;
import com.trustfulfillment.entity.SysUser;
import com.trustfulfillment.entity.SysUserOauth;

import java.math.BigDecimal;
import java.util.List;

public interface SysUserService extends IService<SysUser> {

    /**
     * 根据手机号查找用户
     */
    SysUser getByPhone(String phone);

    /**
     * 手机号验证码登录/注册
     *
     * @param phone    手机号
     * @param ip       登录IP
     * @param device   设备信息
     * @param platform 平台
     * @return 登录结果
     */
    LoginResultDTO loginByPhoneCode(String phone, String ip, String device, String platform);

    /**
     * 手机号密码登录
     *
     * @param phone    手机号
     * @param password 密码
     * @param ip       登录IP
     * @param device   设备信息
     * @param platform 平台
     * @return 登录结果
     */
    LoginResultDTO loginByPhonePassword(String phone, String password, String ip, String device, String platform);

    /**
     * 微信登录
     *
     * @param oauth    OAuth记录
     * @param ip       登录IP
     * @param device   设备信息
     * @param platform 平台
     * @return 登录结果
     */
    LoginResultDTO loginByWechat(SysUserOauth oauth, String ip, String device, String platform);

    /**
     * 绑定手机号 (微信用户绑定)
     *
     * @param phone   手机号
     * @param openid  微信openid
     * @param merge   是否合并已存在的账号
     * @param ip      IP
     * @param device  设备信息
     * @param platform 平台
     * @return 登录结果
     */
    LoginResultDTO bindPhone(String phone, String openid, boolean merge, String ip, String device, String platform);

    /**
     * 设置/修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 原密码 (修改密码时必填)
     * @param newPassword 新密码
     */
    void setPassword(Long userId, String oldPassword, String newPassword);

    /**
     * 更新用户登录信息
     */
    void updateLoginInfo(Long userId, String ip);

    /**
     * 构建用户信息DTO
     */
    LoginResultDTO.UserInfo buildUserInfo(SysUser user);

    /**
     * 更新余额
     */
    boolean updateBalance(Long userId, BigDecimal amount, boolean isAdd);

    /**
     * 冻结/解冻金额
     */
    boolean updateFrozenAmount(Long userId, BigDecimal amount, boolean isFreeze);

    /**
     * 根据角色统计数量
     */
    Long countByRole(String role);

    /**
     * 统计今日新增用户
     */
    Long countNewUsersToday();

    /**
     * 获取待审核商家列表
     */
    List<SysUser> listPendingMerchants(int limit);

    /**
     * 分页获取商家列表
     */
    IPage<SysUser> pageMerchants(int page, int size, Integer status, String keyword);

    /**
     * 分页获取用户列表
     */
    IPage<SysUser> pageUsers(int page, int size, String keyword);
}
