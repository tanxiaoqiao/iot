package com.honeywell.fireiot.water;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:04 PM 3/20/2019
 */
@Data
public class WaterEventStatsBo {
    // 起始时间
    private Long startDateTime;
    // 中止时间
    private Long endDateTime;
    // 事件类型（火警/故障/隔离）
    private String description;
    // 事件状态（Add/Del）Add：事件新增 Del：事件恢复
    private String fieldName;
    private String eui;

    public WaterEventStatsBo(Long startDateTime, Long endDateTime, String description, String fieldName, String eui) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.description = description;
        this.fieldName = fieldName;
        this.eui = eui;
    }
}
