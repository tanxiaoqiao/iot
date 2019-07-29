package com.honeywell.fireiot.integrationService.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 2:56 PM 12/6/2018
 */
@Component
public class RabbitConfig {

    @Autowired
    private RabbitmqProps rabbitmqProps;

    @Bean
    public Queue queueFire() {
        return new Queue(rabbitmqProps.getFireQueue());
    }

    @Bean
    public Queue queueWater() {
        return new Queue(rabbitmqProps.getWaterQueue());
    }

    @Bean
    public FanoutExchange exchangeFire() {
        return new FanoutExchange(rabbitmqProps.getFireExchange());
    }

    @Bean
    public FanoutExchange exchangeWater() {
        return new FanoutExchange(rabbitmqProps.getWaterExchange());
    }


    @Bean
    Binding bindingExchangeMessageDirectF(@Qualifier("queueFire") Queue queueMessage, @Qualifier("exchangeFire") FanoutExchange exchange) {
        return BindingBuilder.bind(queueMessage).to(exchange);
    }

    @Bean
    Binding bindingExchangeMessageDirectW(@Qualifier("queueWater") Queue queueMessage, @Qualifier("exchangeWater") FanoutExchange exchange) {
        return BindingBuilder.bind(queueMessage).to(exchange);
    }
}
