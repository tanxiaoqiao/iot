package com.honeywell.fireiot.fire.entity;

import com.honeywell.fireiot.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 设备事件统计
 */
@Data
@Entity
@Table(name = "fire_event_count")
public class FireEventCount extends BaseEntity<FireEventCount> {

    private String deviceNo;

    private String eventType;

    private Long countNumber;

    private String countDate;
    public FireEventCount(){

    }
    public FireEventCount(String deviceNo,String eventType,String countDate,Long countNumber){
        this.deviceNo=deviceNo;
        this.eventType=eventType;
        this.countDate=countDate;
        this.countNumber=countNumber;
    }

}
