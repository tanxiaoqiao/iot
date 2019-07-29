package com.honeywell.fireiot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Entity
@Table(name = "wo_maintenance")
@Data
public class Maintenance extends BaseEntity<Maintenance> {

    @Column
    private String name;

    @Column(name = "workteam_id")
    private String workteamId;
    @Column(name = "workteam_name")
    private String workteamName;
    @Column
    private ArrayList<Step> steps;
    @Column
    private String description;
    @Column
    private Integer level = 0;

    //设备信息
    private ArrayList<Long> deviceIds;

    //空间位置
    private ArrayList<Long> locationIds;
    //周期间隔
    @Column
    private Integer times;

    //周期类型 0/1/2/3 日周月年
    @Column
    private Integer type;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Timestamp startTime;

    //是否自动生成工单
    @Column
    private Boolean isAuto;

    //提前生成天数
    @Column
    private Integer days;

    //自动存档
    @Column
    private Boolean saveAuto;

    //预估工作时间
    @Column
    private Integer workDays;

    //有效维护时间
    @Column
    private Integer startMonth;

    //有效维护时间
    @Column
    private Integer endMonth;

}
