package com.honeywell.fireiot.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeywell.fireiot.dto.SpotVo;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Table(name = "wo_polling")
@Entity
@Data
public class Polling extends BaseEntity<Polling> implements Serializable {


    //巡检名称
    @Column(unique = true)
    String name;
    //创建者
    String creator;

    //巡检工作组id
    @Column(name = "workteam_id")
    ArrayList<String> workteamId;
    //巡检员工id
    @Column(name = "person_ids")
    ArrayList<String> personIds;
    //未完成提醒工作组id
    @Column(name = "remindteam_id")
    ArrayList<String> remindteamId;
    //未完成提醒员工id
    @Column(name = "reminder_ids")
    ArrayList<String> reminderIds;
    //提前生成任务时间
    @Column(name = "pre_remind")
    Integer preRemind;
    //结束前提醒时间
    @Column(name = "end_remind")
    Integer endRemind;
    //激活状态true激活
    Boolean activated = true;

    //周期类型0非周期，2间隔周期 1固定周期
    @Column(name = "regular_type")
    Integer regularType;
    //非周期时间
    @Column(name = "un_regular_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    ArrayList<UnregularTime> unRegularTimes;

    //quartz需要用到
    @Column(name = "job_group_name")
    String jobGroupName;
    //节假日使用ture 使用
    @Column(name = "use_holiday")
    Boolean useHoliday = false;
//    //额外节假日
//    @Column(name = "extra_holiday")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    ArrayList<Timestamp> extraHoliday;


    //预计开始时间
    @Column(name = "activite_start")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp activiteStart;

    //开始分钟
    @Column(name = "pre_minute")
    String preMinute;

    //结束分钟
    @Column(name = "end_minute")
    String endMinute;

    // 点位任务
    @Column(name = "spot_task")
    ArrayList<SpotVo> spotTask;
    @Column(name = "spot_counts")
    Integer spotCount;
    //重复周期类型 0天1周2月
    @Column(name = "job_type")
    Integer jobType;
    //重复次数
    Integer times;
    @Column(name = "day_of_weeks")
    private Integer[] dayOfWeeks;
    @Column(name = "day_of_months")
    private Integer[] dayOfMonths;

}
