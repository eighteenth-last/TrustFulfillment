package com.trustfulfillment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 手机号密码登录请求
 */
public class PhonePasswordLoginDTO {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;
    private String device;
    private String platform;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
}
