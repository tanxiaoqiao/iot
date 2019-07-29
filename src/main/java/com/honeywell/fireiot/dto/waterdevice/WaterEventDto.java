package com.honeywell.fireiot.dto.waterdevice;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 10:44 AM 4/16/2019
 */
@Data
public class WaterEventDto {
    private Long id;

    private String eui;

    private String fieldName;

    private String description;

    /**
     * ADD or DEL
     *
     */
    private String type;

    private Double max;

    private Double min;

    private Double value;

    private Long startTime;

    private Long endTime;

    private String unit;

    // 设备标签
    private String label;
    // 设备编号
    private String deviceNo;
}
