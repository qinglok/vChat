package me.linx.vchat.bean;

import javax.persistence.*;

@Entity
@Table(name = "user_profile")
public class UserProfile extends Bean {

    @Column(name = "nick_name", length = 50)
    private String nickName;

    @Column(name = "secret_question", length = 100, columnDefinition = "varchar(100) DEFAULT '' COMMENT '密保问题'")
    private String secretQuestion ;

    @Column(name = "secret_answer", length = 100, columnDefinition = "varchar(100) DEFAULT '' COMMENT  '密保答案'")
    private String secretAnswer ;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "head_img_file_id")
    private FileWrapper headImg;

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
