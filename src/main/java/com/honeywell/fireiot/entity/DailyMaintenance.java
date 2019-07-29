package com.honeywell.fireiot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;


/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Entity
@Table(name = "wo_daily_maintenance")
@Data
public class DailyMaintenance implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp startTime;
    @Column
    private Long maintenanceId;

    @Column
    private String name;
    @Column
    private Boolean isAuto;

    /**
     *  提前生成天数
     */
    @Column
    private Integer days;

    /**
     *  预估工作时间
     */
    @Column
    private  Integer workDays;

    @Column(name = "workorder_id")
    private  Long workorderId;



}
