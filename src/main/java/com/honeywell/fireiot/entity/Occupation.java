package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @project: fire-user
 * @name: Occupation
 * @author: dexter
 * @create: 2018-12-06 15:29
 * @description:
 **/
@Entity
@Table(name = "us_occupation")
@Data
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Where(clause = " deleted = 0 ")
@EntityListeners(AuditingEntityListener.class)
public class Occupation implements Serializable {

    private static final long serialVersionUID = -411111222222221L;

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    protected String id;

    @NotBlank(message = "occupation_name_not_null")
    @Column(unique = true)
    private String name;

    private String description;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;

    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updateTime;

    /**
     * 0 - 未删除
     * 1 - 删除
     */
    @JsonIgnore
    private Integer deleted = 0;
}
