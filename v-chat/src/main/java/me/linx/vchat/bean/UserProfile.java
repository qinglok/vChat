package me.linx.vchat.bean;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_profile")
public class UserProfile extends Bean {

    @Column(name = "password_encode",length = 500)
    @NotNull
    private String passwordEncode;

    public String getPasswordEncode() {
        return passwordEncode;
    }

    public void setPasswordEncode(String passwordEncode) {
        this.passwordEncode = passwordEncode;
    }
}
