package com.trustfulfillment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 手机号验证码登录请求
 */
public class PhoneLoginDTO {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{4,6}$", message = "验证码格式不正确")
    private String code;
    private String device;
    private String platform;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
}
