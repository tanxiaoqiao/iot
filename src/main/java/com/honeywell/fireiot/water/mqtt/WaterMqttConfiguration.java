package com.honeywell.fireiot.water.mqtt;

import com.honeywell.fireiot.water.mqtt.bean.WaterMqttMessageConverter;
import com.honeywell.fireiot.water.mqtt.bean.WaterMqttProcessor;
import com.honeywell.fireiot.water.mqtt.property.MqttClientProperties;
import com.honeywell.fireiot.water.mqtt.property.MqttPublishProperties;
import com.honeywell.fireiot.water.mqtt.property.MqttSubscribeProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({
        MqttClientProperties.class,
        MqttSubscribeProperties.class,
        MqttPublishProperties.class
})
public class WaterMqttConfiguration {

    @Autowired
    private MqttClientProperties mqttClientProperties;

    @Autowired
    private MqttSubscribeProperties mqttSubscribeProperties;

    @Autowired
    private MqttPublishProperties mqttPublishProperties;

    public MqttPahoClientFactory mqttPahoClientFactory(){
        log.info("water mqtt client: {}", mqttClientProperties);
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(mqttClientProperties.getUrl());
        options.setUserName(mqttClientProperties.getUsername());
        options.setPassword(mqttClientProperties.getPassword().toCharArray());
        options.setCleanSession(mqttClientProperties.isCleanSession());
        options.setConnectionTimeout(mqttClientProperties.getConnectionTimeout());
        options.setKeepAliveInterval(mqttClientProperties.getKeepAliveInterval());
        factory.setConnectionOptions(options);
        return factory;
    }


    public DefaultPahoMessageConverter messageConverter() {
        DefaultPahoMessageConverter converter = new WaterMqttMessageConverter();
        return converter;
    }


    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        log.info("water mqtt subscriber: {}", mqttSubscribeProperties);
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttSubscribeProperties.getClientId(),
                mqttPahoClientFactory(), mqttSubscribeProperties.getTopics());
        adapter.setQos(mqttSubscribeProperties.getQos());
        adapter.setConverter(messageConverter());
        adapter.setOutputChannelName(WaterMqttProcessor.INPUT);
        return adapter;
    }



}
