package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/**
 * 设备类型
 *
 */
@Data
@Entity
@Table(name = "dev_device_type")
@ToString(exclude = {"systemType"})
public class DeviceType extends BaseEntity<DeviceType>{
    // 设备类型名称
    private String name;
    // 设备类型描述
    private String description;
   //设备类型编码
    @Column(name = "type_code")
    private String code;
    //是否报警
    private Boolean alarm;

    // 第二版新增
    @OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="deviceType" ,orphanRemoval = true)
    private List<ModelNumber> modelNumbers;//设备型号

    @ManyToOne
    @JoinColumn(name="system_type_id")
    @JsonIgnore
    private SystemType systemType;

    @OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="deviceType" ,orphanRemoval = true)
    private List<EventType> eventTypes;//事件类型


}
