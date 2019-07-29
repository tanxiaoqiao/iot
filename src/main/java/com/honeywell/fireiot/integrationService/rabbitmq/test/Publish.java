package com.honeywell.fireiot.integrationService.rabbitmq.test;

import com.alibaba.fastjson.JSON;
import com.honeywell.fireiot.fire.dto.FireEventDto;
import com.honeywell.fireiot.integrationService.rabbitmq.RabbitmqProps;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:59 PM 4/25/2019
 */
@Component
public class Publish {
    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    private RabbitmqProps rabbitmqProps;

//    @Scheduled(fixedDelay = 5000, initialDelay = 500)
    public void sendMessage() {
        FireEventDto fireEventDto = new FireEventDto();
        fireEventDto.setCreateDatetime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        fireEventDto.setEventId(RandomUtils.nextLong());
        fireEventDto.setBuilding("B10");
        fireEventDto.setDeviceId("火系统"+RandomUtils.nextInt());
        fireEventDto.setDeviceLabel("火系统"+RandomUtils.nextInt());
        fireEventDto.setDeviceType("烟感");
        fireEventDto.setEventStatus("Add");
        fireEventDto.setFloor("1F");
        fireEventDto.setEventType(RandomStringUtils.random(8,"ABCDEFabcdef"));
        String json = JSON.toJSONString(fireEventDto);
        amqpTemplate.send(rabbitmqProps.getFireExchange(),"",new Message(json.getBytes(),new MessageProperties()));
        System.out.println("send Fire message");

        Map<String, Object> data = new HashMap<>(16);
        data.put("max", RandomUtils.nextDouble());
        data.put("min", RandomUtils.nextDouble());
        data.put("value", RandomUtils.nextDouble());
        data.put("unit", "kpa");
        data.put("eventType", "水压高位异常");
        data.put("eventStatus", "Add");
        long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        data.put("createDatetime", seconds);
        data.put("deviceLabel", "水系统"+RandomUtils.nextInt());
        data.put("deviceId","水系统"+RandomUtils.nextInt());
        data.put("deviceType", "电池");

            data.put("building", "纯水房");
            data.put("floor", "1F");
        amqpTemplate.send(rabbitmqProps.getWaterExchange(),"",new Message(JSON.toJSONString(data).getBytes(),new MessageProperties()));
        System.out.println("send Water message");
    }





}
