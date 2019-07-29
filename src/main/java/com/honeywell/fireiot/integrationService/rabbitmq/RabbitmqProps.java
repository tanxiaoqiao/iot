package com.honeywell.fireiot.integrationService.rabbitmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 8:51 AM 4/26/2019
 */
@Data
@ConfigurationProperties(prefix = "rabbitmq")
@Component
public class RabbitmqProps {
    private String waterExchange;
    private String fireExchange;
    private String waterQueue;
    private String fireQueue;
}
