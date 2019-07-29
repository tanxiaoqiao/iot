package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * @ClassName PatrolSpotInspect
 * @Description 巡检计划 点位 巡检内容关联表
 * @Author Monica Z
 * @Date 2019/1/21 14:17
 */
@Table(name = "wo_patrol_spot_inspect")
@Entity
@Data
public class PatrolSpotInspect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * 巡检计划id
     */
    @Column
    private long patrolId;
    /**
     * 点位id
     */
    @Column
    private long spotId;
    /**
     * 巡检内容id list
     */
    @Column
    private ArrayList<Long> inspectIds;
    /**
     * 巡检计划创建时间
     */
    @Column
    private Date createTime;
}
