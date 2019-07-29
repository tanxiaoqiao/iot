package com.honeywell.fireiot.integrationService.rabbitmq.test;

import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 3:57 PM 12/6/2018
 */
@Component
public class Receive {

//    @RabbitListener(queues = {"fanout-event-water", "fanout-event-fire"})
    public void processMessage(byte[] content) throws Exception {
        System.out.println("receive message: " + new String(content, "utf-8"));
        System.out.println("over");
    }

}
