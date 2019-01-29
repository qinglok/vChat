package me.linx.vchat.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "file_wrapper")
public class FileWrapper extends Bean {

    @Column(name = "name", length = 100, unique = true)
    private String name;

    @Column(name = "md5", length = 100, unique = true)
    private String md5;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
