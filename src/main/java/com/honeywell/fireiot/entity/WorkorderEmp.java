package com.honeywell.fireiot.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Data
@Table(name = "workorder_emp")
@Entity
public class WorkorderEmp implements Serializable {
    @Id
    @Column
    private String workorderId;
    @Column
    private String employeeId;
    @Column
    private String creator;

    @Column
    private String workTeamId;

    @Column
    private String description;

    @Column
    private Integer auditStatus;

    @Column
    private Integer status;
    @Column
    private String title;
    @Column
    private String workTeamName;

    @Column
    private Integer type;

    @Column
    private Integer level;
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTime;


}
