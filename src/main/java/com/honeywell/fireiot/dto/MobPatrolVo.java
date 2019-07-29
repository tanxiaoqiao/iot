package com.honeywell.fireiot.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

/**
 * @author: create by kris
 * @description:
 * @date:3/7/2019
 */
@Data
public class MobPatrolVo {


    //巡检名称
    String name;
    //创建者
    String creator;

    Long pollingId;

    Long Id;


    //预计开始时间 时分
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Timestamp preEndTime;

    //预计结束时间 时分
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    Timestamp preStartTime;



}
