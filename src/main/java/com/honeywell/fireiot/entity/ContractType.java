package com.honeywell.fireiot.entity;

import com.honeywell.fireiot.constant.NotifyWayEnum;
import com.honeywell.fireiot.constant.ReminderTypeEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:06 PM 1/17/2019
 */
@Entity
@Table(name = "con_contractType")
@Data
public class ContractType extends BaseEntity<ContractType> {
    private String name ; //合同名字

    private String description;//合同描述

    private Boolean isRemind = false; //是否开启提醒

    private Boolean isNotify = false;//是否通知业务员

    @Enumerated(EnumType.STRING)
    private ReminderTypeEnum reminderType; //通知人员类别

    private ArrayList<String> reminders; //通知人员

    private ArrayList<Long> days ;//提前通知天数类别

    @Enumerated(EnumType.STRING)
    private NotifyWayEnum notifyWay; //通知类型


    @CreatedDate
    private Timestamp createTime;

    @LastModifiedDate
    private Timestamp updateTime;


    private long formStructureId;



}
