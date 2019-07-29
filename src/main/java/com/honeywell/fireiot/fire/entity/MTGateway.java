package com.honeywell.fireiot.fire.entity;

import com.honeywell.fireiot.entity.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author ： YingZhang
 * @Description: 网关配置
 * @Date : Create in 4:22 PM 12/14/2018
 */
@Data
@Entity
@Table(name = "mt_gateway")
public class MTGateway extends BaseEntity<MTGateway> {
    // 网关号就是ID
    // 每次最大读取寄存器数量，不能超过125
    @Column(name = "register_limit")
    private int limit = 120;
    // 每次读取的间隔时间
    private int pointSleepTime = 200;
    // 每轮读取的间隔时间
    private int intervalSleepTime = 3000;
    // modbusTcp ip
    private String ip;
    // modbusTcp port
    private int port;
    // IP位置
    private String ipAddress;
    // 栋别
    private String building;
}
