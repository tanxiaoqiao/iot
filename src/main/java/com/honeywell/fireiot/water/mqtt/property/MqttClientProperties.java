package com.honeywell.fireiot.water.mqtt.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Data
@ConfigurationProperties("water.mqtt")
public class MqttClientProperties {

    private String[] url = new String[]{"tcp://localhost:1883"};

    private String username = "admin";

    private String password = "password";

    private boolean cleanSession = true;

    private int connectionTimeout = 30;

    private int keepAliveInterval = 60;
}
