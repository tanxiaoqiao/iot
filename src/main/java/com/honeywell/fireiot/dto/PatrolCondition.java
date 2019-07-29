package com.honeywell.fireiot.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author: create by kris
 * @description:
 * @date:1/8/2019
 */
@Data
public class PatrolCondition {

    //实际结束时间
    Timestamp actEndTime;
    //实际开始时间
    Timestamp actStartTime;

    //预计开始时间 时分
    Timestamp preEndTime;

    //预计结束时间 时分
    Timestamp preStartTime;

    //巡检状态
    Integer status;

    //点位状态 0正常  1异常  2漏检  3补检
    Integer spotStatus;
    Integer pi;
    Integer ps;

}
