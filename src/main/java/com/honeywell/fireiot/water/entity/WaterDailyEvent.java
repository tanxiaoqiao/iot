package com.honeywell.fireiot.water.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:41 PM 3/22/2019
 */
@Data
@Entity
@Table(name =  "w_daily_event")
public class WaterDailyEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createTime;

    // 统计日期
    private String statsDate;
    // 统计类型： 日:0  月:1
    private int statsType;

    // 低位异常
    private long lowException;
    // 高位异常
    private long highException;

    private String eui;
}
