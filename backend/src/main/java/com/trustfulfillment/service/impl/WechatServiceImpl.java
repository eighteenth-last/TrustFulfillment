package com.trustfulfillment.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.trustfulfillment.dto.WechatLoginDTO;
import com.trustfulfillment.entity.SysUserOauth;
import com.trustfulfillment.mapper.SysUserOauthMapper;
import com.trustfulfillment.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WechatServiceImpl implements WechatService {

    private static final Logger log = LoggerFactory.getLogger(WechatServiceImpl.class);

    @Autowired
    private SysUserOauthMapper oauthMapper;

    @Value("${wechat.mp.appid:}")
    private String appid;

    @Value("${wechat.mp.secret:}")
    private String secret;

    @Value("${wechat.mp.mock-mode:true}")
    private boolean mockMode;

    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    public Code2SessionResult code2Session(String code) {
        Code2SessionResult result = new Code2SessionResult();

        if (mockMode) {
            // 模拟模式：返回模拟的openid
            log.info("【模拟微信登录】code: {}", code);
            result.setOpenid("mock_openid_" + code);
            result.setSessionKey("mock_session_key_" + System.currentTimeMillis());
            result.setUnionid(null);
            return result;
        }

        // 真实请求微信接口
        try {
            String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    CODE2SESSION_URL, appid, secret, code);

            String response = HttpUtil.get(url, 5000);
            log.info("微信code2session响应: {}", response);

            JSONObject json = JSONUtil.parseObj(response);
            result.setOpenid(json.getStr("openid"));
            result.setSessionKey(json.getStr("session_key"));
            result.setUnionid(json.getStr("unionid"));
            result.setErrcode(json.getInt("errcode"));
            result.setErrmsg(json.getStr("errmsg"));

            if (!result.isSuccess()) {
                log.error("微信登录失败: {} - {}", result.getErrcode(), result.getErrmsg());
            }
        } catch (Exception e) {
            log.error("调用微信code2session接口失败", e);
            result.setErrcode(-1);
            result.setErrmsg("网络请求失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional
    public SysUserOauth getOrCreateOauth(String openid, String unionid, String oauthType,
                                          WechatLoginDTO.WechatUserInfo userInfo, String sessionKey) {
        // 先查询是否已存在
        SysUserOauth oauth = oauthMapper.selectOne(
                new LambdaQueryWrapper<SysUserOauth>()
                        .eq(SysUserOauth::getOauthType, oauthType)
                        .eq(SysUserOauth::getOpenid, openid)
        );

        if (oauth != null) {
            // 更新session_key和用户信息
            LambdaUpdateWrapper<SysUserOauth> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysUserOauth::getId, oauth.getId())
                    .set(SysUserOauth::getSessionKey, sessionKey);

            if (userInfo != null) {
                updateWrapper.set(SysUserOauth::getNickname, userInfo.getNickName())
                        .set(SysUserOauth::getAvatar, userInfo.getAvatarUrl())
                        .set(SysUserOauth::getGender, userInfo.getGender())
                        .set(SysUserOauth::getCountry, userInfo.getCountry())
                        .set(SysUserOauth::getProvince, userInfo.getProvince())
                        .set(SysUserOauth::getCity, userInfo.getCity());
            }

            oauthMapper.update(null, updateWrapper);

            // 重新查询返回最新数据
            return oauthMapper.selectById(oauth.getId());
        }

        // 创建新记录
        oauth = new SysUserOauth();
        oauth.setOauthType(oauthType);
        oauth.setOpenid(openid);
        oauth.setUnionid(unionid);
        oauth.setSessionKey(sessionKey);

        if (userInfo != null) {
            oauth.setNickname(userInfo.getNickName());
            oauth.setAvatar(userInfo.getAvatarUrl());
            oauth.setGender(userInfo.getGender());
            oauth.setCountry(userInfo.getCountry());
            oauth.setProvince(userInfo.getProvince());
            oauth.setCity(userInfo.getCity());
        }

        oauthMapper.insert(oauth);
        return oauth;
    }

    @Override
    @Transactional
    public void bindUser(Long oauthId, Long userId) {
        oauthMapper.update(null,
                new LambdaUpdateWrapper<SysUserOauth>()
                        .eq(SysUserOauth::getId, oauthId)
                        .set(SysUserOauth::getUserId, userId)
        );
    }
}
