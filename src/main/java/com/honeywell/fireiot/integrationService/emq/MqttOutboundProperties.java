package com.honeywell.fireiot.integrationService.emq;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 3:14 PM 6/5/2018
 */
@Data
public class MqttOutboundProperties {
    private String urls;
    private String username;
    private String password;
    private String clientId;
    private String topic;

    private boolean cleanSession = true;

    private int connectionTimeout = 30;

    private int keepAliveInterval = 60;
}
