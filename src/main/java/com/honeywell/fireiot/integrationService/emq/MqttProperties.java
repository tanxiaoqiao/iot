package com.honeywell.fireiot.integrationService.emq;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 3:04 PM 6/5/2018
 */
@ConfigurationProperties(prefix = "com.mqtt")
@Component
public class MqttProperties {
    private MqttOutboundProperties outbound;

    public MqttOutboundProperties getOutbound() {
        return outbound;
    }

    public void setOutbound(MqttOutboundProperties outbound) {
        this.outbound = outbound;
    }
}


