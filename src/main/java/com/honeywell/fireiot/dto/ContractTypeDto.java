package com.honeywell.fireiot.dto;


import com.honeywell.fireiot.constant.NotifyWayEnum;
import com.honeywell.fireiot.constant.ReminderTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 9:48 AM 1/18/2019
 */
@Data
@ApiModel(value = "contractType请求对象", description = "contractType 请求对象描述")
public class ContractTypeDto {
    @ApiModelProperty(value = "id")
    private  Long id;

    @ApiModelProperty(value = "合同名字")
    private String name ; //合同名字

    @ApiModelProperty( value = "合同描述")
    private String description;//合同描述

    @ApiModelProperty( value = "是否开启提醒")
    private Boolean isRemind = false; //是否开启提醒

    @ApiModelProperty( value = "是否通知业务员")
    private Boolean isNotify = false;//是否通知业务员

    @ApiModelProperty( value = "通知人员类别")
    @Enumerated(EnumType.STRING)
    private ReminderTypeEnum reminderType; //通知人员类别

    @ApiModelProperty( value = "通知人员")
    private ArrayList<String> reminders; //通知人员Id

    @ApiModelProperty(value = "通知人员名字")
    private ArrayList<String> reminderName;//通知人员名字

    @ApiModelProperty(value = "提前通知天数列表")
    private ArrayList<Long> days ;//提前通知天数列表

    @ApiModelProperty( value = "通知类型")
    @Enumerated(EnumType.STRING)
    private NotifyWayEnum notifyWay; //通知类型

    private long formStructureId;


}
