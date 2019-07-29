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
 * @name: EmployeeRelations
 * @author: dexter
 * @create: 2018-12-06 15:41
 * @description:
 **/
@Entity
@Table(name = "us_employee_relations")
@Data
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
@Where(clause = " deleted = 0 ")
@EntityListeners(AuditingEntityListener.class)
public class EmployeeRelations {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    protected String id;

    @NotBlank(message = "employee_id_not_null")
    private String employeeId;

    private String occupationId;

    private String departmentId;

    /**
     * 空间位置id
     * 需要存储最后一级id
     */
    private String positionId;

    /**
     * id以|分隔
     */
    private String workTeamIds;

    private String userId;

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
