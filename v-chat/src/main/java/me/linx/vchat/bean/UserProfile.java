package me.linx.vchat.bean;

import javax.persistence.*;

@SuppressWarnings("unused")
@Entity
@Table(name = "user_profile")
public class UserProfile extends Bean {

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "secret_question", length = 100, columnDefinition = "varchar(100) DEFAULT '' COMMENT '密保问题'")
    private String secretQuestion ;

    @Column(name = "secret_answer", length = 100, columnDefinition = "varchar(100) DEFAULT '' COMMENT  '密保答案'")
    private String secretAnswer ;

    @ManyToOne()
    @JoinColumn(name = "avatar_file_id")
    private FileWrapper avatar;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public FileWrapper getAvatar() {
        return avatar;
    }

    public void setAvatar(FileWrapper avatar) {
        this.avatar = avatar;
    }
}
