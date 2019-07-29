package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:17 PM 12/17/2018
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<T extends BaseEntity> implements Serializable, Comparable<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreatedDate
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;

    @LastModifiedDate
    @Column(name = "update_time")
    private Timestamp updateTime;

    @Override
    public int compareTo(T obj) {
        if (obj == null) {
            return -1;
        }
        if (this.equals(obj)) {
            return 0;
        }

        // 按照类名正序排列
        if (!obj.getClass().getName().equals(getClass().getName())) {
            return getClass().getName().hashCode() - obj.getClass().getName().hashCode();
        }

        T o = (T) obj;

        // ID为Null的对象放在前
        if (o.getId() == null) {
            if (getId() != null) {
                return 1;
            }
        }
        if (o.getId() != null) {
            if (getId() == null) {
                return -1;
            }
        }
        // 比较的对象ID都为NULL时，根据hashcode倒序排列
        if (o.getId() == null && getId() == null) {
            return o.hashCode() - hashCode();
        }

        // ID都不为NULL时，根据id倒序排列
        return o.getId().intValue() - getId().intValue();
    }
}
