package com.honeywell.fireiot.water.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日统计数据
 *
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Data
@Entity
@Table(
        name =  "w_daily_data",
        indexes = {
                @Index(name = "WDD_INDEX_EUI_NAME_TIMESTAMP", columnList = "eui,fieldName,timestamp")
        })
public class WaterDailyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eui;

    private String fieldName;

    private String value;

    private String timestamp;
    private LocalDateTime createTime;

    public WaterDailyData() {}

    public WaterDailyData(String eui, String fieldName, String value, String timestamp, LocalDateTime createTime) {
        this.eui = eui;
        this.fieldName = fieldName;
        this.value = value;
        this.timestamp = timestamp;
        this.createTime = createTime;
    }
}
