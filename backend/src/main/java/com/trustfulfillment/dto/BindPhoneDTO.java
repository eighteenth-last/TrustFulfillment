package com.trustfulfillment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 绑定手机号请求 (微信用户绑定手机号)
 */
public class BindPhoneDTO {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{4,6}$", message = "验证码格式不正确")
    private String code;
    private String openid;
    private Boolean merge = false;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getOpenid() { return openid; }
    public void setOpenid(String openid) { this.openid = openid; }

    public Boolean getMerge() { return merge; }
    public void setMerge(Boolean merge) { this.merge = merge; }
}
