package com.honeywell.fireiot.water.mqtt.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Data
@ConfigurationProperties("water.mqtt.publish")
public class MqttPublishProperties {

    private String clientId = "water.client.mqtt.output";

    private String topic = "default";

    private int qos = 1;

    private boolean retained = false;

    private String charset = "UTF-8";

    private boolean async = false;

    private int completionTimeout = 3000;
}
