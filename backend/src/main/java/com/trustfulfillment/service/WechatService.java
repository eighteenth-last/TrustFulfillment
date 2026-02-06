package com.trustfulfillment.service;

import com.trustfulfillment.dto.WechatLoginDTO;
import com.trustfulfillment.entity.SysUserOauth;

/**
 * 微信服务接口
 */
public interface WechatService {

    /**
     * 小程序登录 - 通过code获取openid
     *
     * @param code 微信登录code
     * @return Code2SessionResult
     */
    Code2SessionResult code2Session(String code);

    /**
     * 获取或创建OAuth记录
     *
     * @param openid    微信openid
     * @param unionid   微信unionid
     * @param oauthType OAuth类型
     * @param userInfo  用户信息
     * @return OAuth记录
     */
    SysUserOauth getOrCreateOauth(String openid, String unionid, String oauthType,
                                   WechatLoginDTO.WechatUserInfo userInfo, String sessionKey);

    /**
     * 绑定用户到OAuth
     *
     * @param oauthId OAuth记录ID
     * @param userId  用户ID
     */
    void bindUser(Long oauthId, Long userId);

    /**
     * code2session响应结果
     */
    class Code2SessionResult {
        private String openid;
        private String sessionKey;
        private String unionid;
        private Integer errcode;
        private String errmsg;

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public boolean isSuccess() {
            return errcode == null || errcode == 0;
        }
    }
}
