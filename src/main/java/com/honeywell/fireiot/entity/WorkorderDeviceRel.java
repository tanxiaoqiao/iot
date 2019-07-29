package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.Value;

import javax.persistence.*;

/**
 * @Author: Kris
 * @Date: 2019-07-05  14:29
 */
@Entity
@Table(name = "wo_workorder_device")
@Data
public class WorkorderDeviceRel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Long deviceId;

    @ManyToOne()
    private Workorder workorders;

    public WorkorderDeviceRel(Long deviceId, Workorder workorders) {
        this.deviceId = deviceId;
        this.workorders = workorders;
    }

    public WorkorderDeviceRel() {
    }
}
