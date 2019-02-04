package me.linx.vchat.model.validation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RegisterModel {
    @NotNull(message = "邮箱不能为空")
    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    @Size(min = 5, max = 50, message = "邮箱长度应在5-50之间")
    private String email;

    @NotNull(message = "密码不能为空")
    @NotEmpty(message = "密码不能为空")
    @Size(min = 4, max = 30, message = "密码长度应在4-30之间")
    private String password;

    @NotNull(message = "密保问题不能为空")
    @NotEmpty(message = "密保问题不能为空")
    @Size(min = 1, max = 50, message = "密保问题长度应在1-50之间")
    private String secretQuestion;

    @NotNull(message = "密保答案不能为空")
    @NotEmpty(message = "密保答案不能为空")
    @Size(min = 1, max = 50, message = "密保答案长度应在1-50之间")
    private String secretAnswer;

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

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
