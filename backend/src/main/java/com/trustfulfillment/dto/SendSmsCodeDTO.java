package com.trustfulfillment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 发送短信验证码请求
 */
public class SendSmsCodeDTO {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotNull(message = "验证码类型不能为空")
    private Integer type;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
}
