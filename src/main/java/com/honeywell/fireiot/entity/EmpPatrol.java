package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:1/8/2019
 */
@Table(name = "wo_emp_patrol")
@Entity
@Data
@ToString(exclude = "patrols")
public class EmpPatrol extends BaseEntity<EmpPatrol> {


    @Column(name = "employee_id")
    String employeeId;

    @ManyToMany(mappedBy = "empPatrols")
    @JsonIgnore
    List<Patrol> patrols;

}
