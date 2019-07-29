package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @Author: Kris
 * @Date: 2019-07-05  14:29
 */
@Entity
@Table(name = "wo_workorder_location")
@Data
public class WorkorderLocationRel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Long locationId;

    @ManyToOne()
    private Workorder workorders;

    public WorkorderLocationRel(Long locationId, Workorder workorders) {
        this.locationId = locationId;
        this.workorders = workorders;
    }

    public WorkorderLocationRel() {
    }
}
