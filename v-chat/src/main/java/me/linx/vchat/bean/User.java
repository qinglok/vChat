package me.linx.vchat.bean;


import javax.persistence.*;

/**
 * linx 2018/9/24 21:04
 */
@Entity
@Table(name = "user")
public class User extends Bean {
    @Column(name = "email", length = 50, unique = true)
    private String email;

    @Column(name = "password_encode",length = 500)
    private String passwordEncode;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private UserProfile userProfile;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordEncode() {
        return passwordEncode;
    }

    public void setPasswordEncode(String passwordEncode) {
        this.passwordEncode = passwordEncode;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
