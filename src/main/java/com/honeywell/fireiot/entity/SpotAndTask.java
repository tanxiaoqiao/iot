package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName SpotAndTask
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/10 13:14
 */
@Table(name = "wo_spot_and_task")
@Entity
@Data
public class SpotAndTask implements Serializable {
    /**
     * id 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private long spotId;
    @Column
    private long taskId;
    @Column
    private Date createTime;
    @Column
    private Date updateTime;
}
