package com.honeywell.fireiot.dto.waterdevice;


import lombok.Data;

import java.util.List;

/**
 * 水系统设备
 *
 * @Author: zhenzhong.wang
 * @Time: 2018/12/14 16:35
 */
@Data
public class WaterDeviceDto {

    private Long id;

    private String deviceNo;        // 设备编号
    private String deviceEUI;       // 水系统设备唯一编号（协议相关）

    private String deviceType;      // 设备类型
    private String deviceId;
    private String deviceLabel;
    private String mapLocation;
    private String locationName;
    private String locationDetail;

    private String applyType;       // 应用类型
    private String head_4;
    private String head_2;
    private String input;           // 投入式
    private String description;     // 描述

    // 值数组，max,min,unit,value,status
    List values;

    private String status; //设备状态
}
