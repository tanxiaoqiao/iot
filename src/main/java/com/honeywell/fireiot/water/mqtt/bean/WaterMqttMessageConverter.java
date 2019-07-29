package com.honeywell.fireiot.water.mqtt.bean;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Slf4j
public class WaterMqttMessageConverter extends DefaultPahoMessageConverter {

    @Override
    protected Object mqttBytesToPayload(MqttMessage mqttMessage) {
        if(log.isDebugEnabled()){
            log.debug(JSON.toJSONString(mqttMessage));
        }
        return JSON.parseObject(mqttMessage.getPayload(), WaterMqttMessage.class);
    }
}

