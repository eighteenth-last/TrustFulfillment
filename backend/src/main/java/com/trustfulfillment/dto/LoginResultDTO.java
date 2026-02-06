package com.trustfulfillment.dto;

import java.math.BigDecimal;

/**
 * 登录结果
 */
public class LoginResultDTO {

    private String token;
    private Boolean isNewUser;
    private Boolean needBindPhone;
    private String openid;
    private UserInfo userInfo;

    // ============ Getters and Setters ============

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Boolean getIsNewUser() { return isNewUser; }
    public void setIsNewUser(Boolean isNewUser) { this.isNewUser = isNewUser; }

    public Boolean getNeedBindPhone() { return needBindPhone; }
    public void setNeedBindPhone(Boolean needBindPhone) { this.needBindPhone = needBindPhone; }

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }

    public UserInfo getUserInfo() { return userInfo; }
    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo; }

    // ============ Builder ============

    public static LoginResultDTOBuilder builder() {
        return new LoginResultDTOBuilder();
    }

    public static class LoginResultDTOBuilder {
        private String token;
        private Boolean isNewUser;
        private Boolean needBindPhone;
        private String openid;
        private UserInfo userInfo;

        public LoginResultDTOBuilder token(String token) { this.token = token; return this; }
        public LoginResultDTOBuilder isNewUser(Boolean isNewUser) { this.isNewUser = isNewUser; return this; }
        public LoginResultDTOBuilder needBindPhone(Boolean needBindPhone) { this.needBindPhone = needBindPhone; return this; }
        public LoginResultDTOBuilder openid(String openid) { this.openid = openid; return this; }
        public LoginResultDTOBuilder userInfo(UserInfo userInfo) { this.userInfo = userInfo; return this; }

        public LoginResultDTO build() {
            LoginResultDTO dto = new LoginResultDTO();
            dto.token = this.token;
            dto.isNewUser = this.isNewUser;
            dto.needBindPhone = this.needBindPhone;
            dto.openid = this.openid;
            dto.userInfo = this.userInfo;
            return dto;
        }
    }

    public static class UserInfo {
        private Long id;
        private String phone;
        private String nickname;
        private String avatar;
        private Integer gender;
        private String role;
        private Integer isMerchant;   // 是否有商家权限
        private Long merchantId;      // 关联商户ID
        private BigDecimal balance;
        private BigDecimal frozenAmount;
        private Integer creditScore;
        private Integer verified;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }

        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }

        public Integer getGender() { return gender; }
        public void setGender(Integer gender) { this.gender = gender; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Integer getIsMerchant() { return isMerchant; }
        public void setIsMerchant(Integer isMerchant) { this.isMerchant = isMerchant; }

        public Long getMerchantId() { return merchantId; }
        public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }

        public BigDecimal getFrozenAmount() { return frozenAmount; }
        public void setFrozenAmount(BigDecimal frozenAmount) { this.frozenAmount = frozenAmount; }

        public Integer getCreditScore() { return creditScore; }
        public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }

        public Integer getVerified() { return verified; }
        public void setVerified(Integer verified) { this.verified = verified; }

        public static UserInfoBuilder builder() {
            return new UserInfoBuilder();
        }

        public static class UserInfoBuilder {
            private Long id;
            private String phone;
            private String nickname;
            private String avatar;
            private Integer gender;
            private String role;
            private Integer isMerchant;
            private Long merchantId;
            private BigDecimal balance;
            private BigDecimal frozenAmount;
            private Integer creditScore;
            private Integer verified;

            public UserInfoBuilder id(Long id) { this.id = id; return this; }
            public UserInfoBuilder phone(String phone) { this.phone = phone; return this; }
            public UserInfoBuilder nickname(String nickname) { this.nickname = nickname; return this; }
            public UserInfoBuilder avatar(String avatar) { this.avatar = avatar; return this; }
            public UserInfoBuilder gender(Integer gender) { this.gender = gender; return this; }
            public UserInfoBuilder role(String role) { this.role = role; return this; }
            public UserInfoBuilder isMerchant(Integer isMerchant) { this.isMerchant = isMerchant; return this; }
            public UserInfoBuilder merchantId(Long merchantId) { this.merchantId = merchantId; return this; }
            public UserInfoBuilder balance(BigDecimal balance) { this.balance = balance; return this; }
            public UserInfoBuilder frozenAmount(BigDecimal frozenAmount) { this.frozenAmount = frozenAmount; return this; }
            public UserInfoBuilder creditScore(Integer creditScore) { this.creditScore = creditScore; return this; }
            public UserInfoBuilder verified(Integer verified) { this.verified = verified; return this; }

            public UserInfo build() {
                UserInfo info = new UserInfo();
                info.id = this.id;
                info.phone = this.phone;
                info.nickname = this.nickname;
                info.avatar = this.avatar;
                info.gender = this.gender;
                info.role = this.role;
                info.isMerchant = this.isMerchant;
                info.merchantId = this.merchantId;
                info.balance = this.balance;
                info.frozenAmount = this.frozenAmount;
                info.creditScore = this.creditScore;
                info.verified = this.verified;
                return info;
            }
        }
    }
}
