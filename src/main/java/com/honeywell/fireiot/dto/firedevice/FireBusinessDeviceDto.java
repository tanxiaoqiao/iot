package com.honeywell.fireiot.dto.firedevice;

import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.entity.SystemType;
import lombok.Data;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 5:04 PM 12/27/2018
 */
@Data
public class FireBusinessDeviceDto {
    private String deviceNo;            // 设备编号(UUID)
    private String deviceLabel;         // 设备标签

    private Long locationId;          // 位置ID


    private DeviceType deviceType;      // 设备类型

    private SystemType systemType;      // 系统类型
}
