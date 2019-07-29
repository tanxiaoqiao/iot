package com.honeywell.fireiot.water.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 水系统上传数据
 *
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Data
@Entity
@Table(name = "w_data", indexes = {
        @Index(columnList = "eui"),
        @Index(columnList = "timestamp")
})
public class WaterData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eui;

    private String fieldName;

    /**
     * 值
     */
    private String value;

    /**
     * 设备上传数据时间戳
     */
    private Long timestamp;

}
