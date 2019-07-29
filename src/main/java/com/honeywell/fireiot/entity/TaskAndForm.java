package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName TaskAndForm
 * @Description 工作内容详情，所包含的巡检内容列表，form指formElement；
 * @Author Monica Z
 * @Date 2019/1/17 09:39
 */

@Table(name = "wo_task_and_form")
@Entity
@Data
public class TaskAndForm implements Serializable {
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private long taskId;
    @Column
    private int type;
    @Column
    private long deviceId;
    @Column
    private Long formElementId;
    @Column
    private Date createTime;
    @Column
    private Date updateTime;
}

