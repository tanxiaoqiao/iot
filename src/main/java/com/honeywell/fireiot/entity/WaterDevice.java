package com.honeywell.fireiot.entity;


import lombok.Data;

import javax.persistence.*;

/**
 * 水系统设备
 *
 * @Author: zhenzhong.wang
 * @Time: 2018/12/14 16:35
 */
@Data
@Entity
@Table(
    name = "water_device",
    indexes = {
        @Index(name = "WD_INDEX_NO", columnList = "deviceNo"),
        @Index(name = "WD_INDEX_EUI", columnList = "deviceEUI"),
    })
public class WaterDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceNo;        // 设备编号
    private String deviceEUI;       // 水系统设备唯一编号（协议相关）

    private String applyType;       // 应用类型
    private String head_4;
    private String head_2;
    private String input;           // 投入式
    private String description;     // 描述

    private String name;

    private String address;

    private String label;

}
