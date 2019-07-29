package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dev_event_type")
@ToString(exclude = {"deviceType"})
public class EventType extends BaseEntity<EventType>{

    private Integer key;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name="device_type_id")
    @JsonIgnore
    private DeviceType deviceType;


}
