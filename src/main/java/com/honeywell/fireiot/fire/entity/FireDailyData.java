package com.honeywell.fireiot.fire.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Author ： YingZhang
 * @Description: 火系统事件统计数据
 * @Date : Create in 8:41 AM 3/20/2019
 */
@Data
@Entity
@Table(name =  "fire_daily_data")
public class FireDailyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createTime;

    // 统计日期
    private String statsDate;
    // 统计类型： 日:0  月:1
    private int statsType;

    // 火警数
    private long countFire;
    // 故障数
    private long countFault;
    // 隔离数
    private long countShield;
    // 全部事件
    private long countAll;
    // 其它事件=全部-火警-故障-隔离
    private long countOther;

    public void setCountOther() {
        this.countOther = countAll-countFire-countFault-countShield;
    }
}
