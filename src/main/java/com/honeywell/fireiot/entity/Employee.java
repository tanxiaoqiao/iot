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
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @project: fire-user
 * @name: Employee
 * @author: dexter
 * @create: 2018-12-06 15:32
 * @description:
 **/
@Entity
@Table(name = "us_employee")
@Data
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Where(clause = " deleted = 0 ")
@EntityListeners(AuditingEntityListener.class)
public class Employee {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    protected String id;

    @NotBlank(message = "employee_name_not_null")
    private String name;

    private String employeeNo;

    private String telephone;

    private String mobile;

    /**
     * 类型
     * 0 - 员工
     */
    private Integer type;

    private String email;

    private String description;

    /**
     * 0 - 在职
     * 1 - 离职
     */
    @NotNull(message = "employee_status_not_null")
    private Integer status;

    private String technicals;

    @CreatedDate
    private Timestamp createTime;

    @LastModifiedDate
    private Timestamp updateTime;

    /**
     * 头像
     */
    private String picture;

    /**
     * 0 - 未删除
     * 1 - 删除
     */
    @JsonIgnore
    private Integer deleted = 0;
}
