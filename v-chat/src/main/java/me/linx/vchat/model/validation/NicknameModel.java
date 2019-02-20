package me.linx.vchat.model.validation;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@SuppressWarnings("unused")
public class NicknameModel {
    @NotNull(message = "昵称不能为空")
    @NotEmpty(message = "昵称不能为空")
    @Size(min = 1, max = 8, message = "昵称长度应在1-8之间")
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
