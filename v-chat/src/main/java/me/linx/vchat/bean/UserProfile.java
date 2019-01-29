package me.linx.vchat.bean;

import javax.persistence.*;

@Entity
@Table(name = "user_profile")
public class UserProfile extends Bean {

    @Column(name = "nick_name", length = 50)
    private String nickName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "head_img_file_id")
    private FileWrapper headImg;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public FileWrapper getHeadImg() {
        return headImg;
    }

    public void setHeadImg(FileWrapper headImg) {
        this.headImg = headImg;
    }
}
