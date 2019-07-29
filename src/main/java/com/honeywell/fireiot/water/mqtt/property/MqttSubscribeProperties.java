package com.honeywell.fireiot.water.mqtt.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Data
@ConfigurationProperties("water.mqtt.subscribe")
public class MqttSubscribeProperties {

    private String clientId = "water.client.mqtt.input";

    private String[] topics = new String[]{"application/0000000000001/node/+/rx"};

    private int[] qos = new int[]{0};

    private boolean binary = false;

    private  String charset = "UTF-8";
}
