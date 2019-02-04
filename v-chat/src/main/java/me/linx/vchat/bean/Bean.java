package me.linx.vchat.bean;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@SuppressWarnings("unused")
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Bean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
