package me.linx.vchat.bean;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * linx 2018/9/24 21:04
 */
@Entity
@Table(name = "user")
public class User extends Bean {

    @Column(name = "email", length = 50, unique = true)
    @NotNull(message = "邮箱不能为空")
    @Email(message = "邮箱格式错误")
    @Size(min = 6, max = 50, message = "邮箱长度必须在6-50之间")
    private String email;

    @Column(name = "password", length = 50)
    @NotNull(message = "密码不能为空")
    @Size(min = 8, max = 50, message = "密码长度必须在8-50之间")
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private UserProfile userProfile;

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

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
