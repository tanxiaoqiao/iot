package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 6:33 PM 1/2/2019
 */
@Data
public class DeviceEventDto {

    // 设备Id
    private String deviceId;
    // 设备标签（设备名称）
    private String deviceLabel;
    // 设备类型
    private String deviceType;
    // 设备状态
    private List<String> deviceEventStatus;
    // 地图上的位置信息（地图位置）
    private String mapLocation;


}
