package com.honeywell.fireiot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.honeywell.fireiot.dto.SpotVo;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Table(name = "wo_patrol")
@Entity
@Data
@ToString(exclude = "empPatrols")
public class Patrol extends BaseEntity<Patrol> {

    //巡检名称
    String name;
    //创建者
    String creator;
    //巡检状态 0未巡检 1进行中 2已完成 3延期完成 4未完成
    Integer status = 0;

    Integer spotStatus;
    //巡检工作组id
    @Column(name = "work_team_ids")
    ArrayList<String> workTeamIds;
    //巡检员工id
    @Column(name = "person_ids")
    ArrayList<String> personIds;

    Long pollingId;

    //实际结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "act_end_time")
    Timestamp actEndTime;
    //实际开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "act_start_time")
    Timestamp actStartTime;

    //预计开始时间 时分
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "pre_end_time")
    Timestamp preEndTime;

    //预计结束时间 时分
    @Column(name = "pre_start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp preStartTime;

    @Column(name = "spot_task")
    ArrayList<SpotVo> spotTask;

    @Column(name = "spot_count")
    Integer spotCount;
    //正常个数
    @Column(name = "normal_nums")
    Integer normalNums = 0;
    //异常个数
    @Column(name = "exception_nums")
    Integer exceptionNums = 0;
    //报修个数
    @Column(name = "repair_nums")
    Integer repairNums = 0;
    //漏检个数
    @Column(name = "miss_nums")
    Integer missNums = 0;
    //补检个数
    @Column(name = "supplement_nums")
    Integer supplementNums = 0;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(name = "wo_patrol_emp_rel", joinColumns = {
            @JoinColumn(name = "patrol_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "emp_patrol_id", referencedColumnName = "id")
    })
    List<EmpPatrol> empPatrols;


}
