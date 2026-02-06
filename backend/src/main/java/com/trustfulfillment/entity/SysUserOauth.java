package com.trustfulfillment.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * 第三方登录绑定实体
 */
@TableName("sys_user_oauth")
public class SysUserOauth {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String oauthType;
    private String openid;
    private String unionid;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String country;
    private String province;
    private String city;
    private String sessionKey;
    private String accessToken;
    private String refreshToken;
    private LocalDateTime tokenExpireTime;
    private String rawData;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ============ Getters and Setters ============

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getOauthType() { return oauthType; }
    public void setOauthType(String oauthType) { this.oauthType = oauthType; }

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }

    public String getUnionid() { return unionid; }
    public void setUnionid(String unionid) { this.unionid = unionid; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getSessionKey() { return sessionKey; }
    public void setSessionKey(String sessionKey) { this.sessionKey = sessionKey; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public LocalDateTime getTokenExpireTime() { return tokenExpireTime; }
    public void setTokenExpireTime(LocalDateTime tokenExpireTime) { this.tokenExpireTime = tokenExpireTime; }

    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
