package me.linx.vchat.model.validation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginAndVerifySecretModel extends LoginModel{

    @NotNull(message = "密保答案不能为空")
    @NotEmpty(message = "密保答案不能为空")
    @Size(min = 1, max = 50, message = "密保答案长度应在1-50之间")
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
