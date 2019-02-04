package me.linx.vchat.bean;

import javax.persistence.*;

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
}
