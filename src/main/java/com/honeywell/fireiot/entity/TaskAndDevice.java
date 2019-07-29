package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName TaskAndDevice
 * @Description 工作任务与设备关联关系表
 * @Author Monica Z
 * @Date 2019/1/17 15:10
 */
@Table(name = "wo_task_and_device")
@Entity
@Data
public class TaskAndDevice implements Serializable {
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private long taskId;
    /**
     * 设备名称
     */
    @Column
    private long deviceId;
    /**
     * 设备编码
     *
     */
    @Column
    private String deviceNo;
    /**
     *  设备标签
     */
    @Column
    private String deviceLabel;
    /**
     * 安装位置id
     */
    @Column
    private Long locationId;
    /**
     * 安装位置名称
     */
    @Column
    private String locationName;
    /**
     * 所属系统名称
     */
    @Column
    private String systemName;

    @Column
    private Date createTime;
    @Column
    private Date updateTime;
}
