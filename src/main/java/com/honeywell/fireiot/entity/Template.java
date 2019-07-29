package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "wo_template")
@Entity
@Data
public class Template {
    /**
     * 模板ID 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /**
     * 模板名称
     */
    @Column(nullable = false)
    private String name;
    /**
     * 模板类型
     * 1 - 清洁
     * 2 - 安保
     * 3 - 设备
     * 4 - 机房
     * 5 - 绿化
     * 6 - 抄表
     */
    @Column
    private Integer type;
    @Column
    private String comments;
    @Column
    private Date createTime;
    @Column
    private Date updateTime;
}
