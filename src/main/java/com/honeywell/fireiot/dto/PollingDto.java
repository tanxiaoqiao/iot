package com.honeywell.fireiot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeywell.fireiot.entity.UnregularTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kris
 * @Date: 2019-06-14  09:59
 */
@Data
public class PollingDto {
    Long id;
    //巡检名称
    String name;
    //创建者
    String creator;

    //巡检工作组id
    ArrayList<String> workteamId;
    List<String> workteamName;
    //巡检员工id
    ArrayList<String> personIds;
    List<String> personNames;
    //未完成提醒工作组id
    ArrayList<String> remindteamId;
    List<String> remindteamName;
    //未完成提醒员工id
    ArrayList<String> reminderIds;
    List<String> reminderNames;
    //提前生成任务时间
    Integer preRemind;
    //结束前提醒时间
    Integer endRemind;
    //激活状态true激活
    Boolean activated = true;

    //周期类型0非周期，2间隔周期 1固定周期
    Integer regularType;
    //非周期时间
    ArrayList<UnregularTime> unRegularTimes;

    //quartz需要用到
    String jobGroupName;
    //节假日使用ture 使用
    Boolean useHoliday = false;
    //额外节假日
    ArrayList<Timestamp> extraHoliday;


    //预计开始时间
    Timestamp activiteStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;

    //开始分钟
    String preMinute;

    //结束分钟
    String endMinute;
    Integer spotCount;
    //重复周期类型 0天1周2月
    Integer jobType;
    //重复次数
    Integer times;
    private Integer[] dayOfWeeks;
    private Integer[] dayOfMonths;

    List<SpotTaskShow> taskDtos;
}
