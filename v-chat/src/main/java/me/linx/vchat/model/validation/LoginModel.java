package me.linx.vchat.model.validation;

import javax.validation.constraints.*;

@SuppressWarnings("unused")
public class LoginModel {
    @NotNull(message = "邮箱不能为空")
    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    @Size(min = 5, max = 50, message = "邮箱长度应在5-50之间")
    private String email;

    @NotNull(message = "密码不能为空")
    @NotEmpty(message = "密码不能为空")
    @Size(min = 4, max = 30, message = "密码长度应在4-30之间")
    private String password;

    @NotNull()
    @NotEmpty()
    private String deviceId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
