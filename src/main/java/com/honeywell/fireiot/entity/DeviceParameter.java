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
@Table(name = "dev_business_device_parameter")
@ToString(exclude = {"businessDevice"})
public class DeviceParameter extends BaseEntity<DeviceParameter> {

    private String name;//参数名

    private String  value;//参数值

    private String  unit;//单位

    @ManyToOne
    @JoinColumn(name="business_device_id")
    @JsonIgnore
    private BusinessDevice businessDevice;
}
