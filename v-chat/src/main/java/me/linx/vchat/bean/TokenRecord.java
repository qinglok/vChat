package me.linx.vchat.bean;

import javax.persistence.*;
import java.util.Date;

@SuppressWarnings("unused")
@Entity
@Table(name = "token_record")
public class TokenRecord extends Bean{
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "device", length = 500)
    private String device;

    @Column(name = "token", length = 500)
    private String token;

    // token 过期时间
    @Column(name = "exp_time")
    private Date expTime;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpTime() {
        return expTime;
    }

    public void setExpTime(Date expTime) {
        this.expTime = expTime;
    }
}
