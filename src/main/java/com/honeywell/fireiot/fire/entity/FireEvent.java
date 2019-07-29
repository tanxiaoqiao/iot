package com.honeywell.fireiot.fire.entity;

import com.honeywell.fireiot.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author ： YingZhang
 * @Description: 火警事件
 * @Date : Create in 1:16 PM 12/28/2018
 */
@Data
@Entity
@Table(name = "fire_event")
public class FireEvent extends BaseEntity<FireEvent> {
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
    // 事件状态（Add/Del）Add：事件新增 Del：事件恢复
    private String eventStatus;
    // 0:有效  1：删除
    private Integer status;
    // 区号
    private String zone;
    // 点号
    private String point;
}
