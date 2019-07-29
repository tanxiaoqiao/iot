package com.honeywell.fireiot.fire.dto;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 7:55 PM 12/28/2018
 */
@Data
public class FireEventDto {
    // 事件Id
    private Long eventId;
    // 自定义设备Id
    private String deviceId;
    // 创建时间
    private Long createDatetime;
    // 楼
    private String building;
    // 层
    private String floor;
    // 设备标签
    private String deviceLabel;
    // 设备类型
    private String deviceType;
    // 事件类型（火警/故障/隔离）
    private String eventType;
    // 事件状态（Add/Del）
    private String eventStatus;
    // 区号
    private String zone;
    // 点号
    private String point;

}
