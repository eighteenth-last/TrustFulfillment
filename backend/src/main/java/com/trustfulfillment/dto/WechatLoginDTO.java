package com.trustfulfillment.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 微信登录请求
 */
public class WechatLoginDTO {

    @NotBlank(message = "code不能为空")
    private String code;
    private WechatUserInfo userInfo;
    private String device;
    private String platform;

    // ============ Getters and Setters ============

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public WechatUserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(WechatUserInfo userInfo) { this.userInfo = userInfo; }

    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public static class WechatUserInfo {
        private String nickName;
        private String avatarUrl;
        private Integer gender;
        private String country;
        private String province;
        private String city;

        public String getNickName() { return nickName; }
        public void setNickName(String nickName) { this.nickName = nickName; }

        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

        public Integer getGender() { return gender; }
        public void setGender(Integer gender) { this.gender = gender; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }
}
