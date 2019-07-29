package com.honeywell.fireiot.water.mqtt.bean;

import lombok.Data;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Data
public class WaterMqttMessage {

    private String devEUI;

    private long timestamp;

    private String data;

}
