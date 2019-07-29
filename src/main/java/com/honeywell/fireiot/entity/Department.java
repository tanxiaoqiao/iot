package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

/**
 * @project: fire-user
 * @name: Department
 * @author: dexter
 * @create: 2018-12-06 10:15
 * @description:
 **/
@Entity
@Table(name = "us_department")
@Data
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Where(clause = "deleted = 0")
@EntityListeners(AuditingEntityListener.class)
public class Department {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    protected String id;

    @NotBlank(message = "department_name_not_null")
    @Column(unique = true)
    private String name;

    private String code;

    private String description;

    private Integer level;

    private String parentId;


    @CreatedDate
    private Timestamp createTime;

    @LastModifiedDate
    private Timestamp updateTime;

    /**
     * 0 - 未删除
     * 1 - 删除
     */
    @JsonIgnore
    private Integer deleted = 0;
}
