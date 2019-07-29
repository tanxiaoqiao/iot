package com.honeywell.fireiot.bo;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 10:19 PM 1/7/2019
 */
@Data
public class WebMessage {
    private long gatewayId;
    // 发生时间
    private Long timeStamp;

    private String eventType;

    // modbusTcp ip
    private String ip;
    // modbusTcp port
    private int port;
    // IP位置
    private String ipAddress;
    // 栋别
    private String building;
}
