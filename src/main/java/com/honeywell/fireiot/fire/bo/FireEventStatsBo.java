package com.honeywell.fireiot.fire.bo;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:04 PM 3/20/2019
 */
@Data
public class FireEventStatsBo {
    // 起始时间
    private Long startDateTime;
    // 中止时间
    private Long endDateTime;
    // 事件类型（火警/故障/隔离）
    private String eventType;
    // 事件状态（Add/Del）Add：事件新增 Del：事件恢复
    private String eventStatus;

    public FireEventStatsBo(Long startDateTime, Long endDateTime, String eventType, String eventStatus) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.eventType = eventType;
        this.eventStatus = eventStatus;
    }
}
