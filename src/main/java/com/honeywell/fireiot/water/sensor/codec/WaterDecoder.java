package com.honeywell.fireiot.water.sensor.codec;

import com.honeywell.fireiot.water.mqtt.bean.WaterMqttMessage;
import com.honeywell.fireiot.water.sensor.bean.WaterKeyValue;

import java.util.function.Consumer;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
public interface WaterDecoder {

    /**
     * 将消息体解码为指定类型，事件处理，数据处理
     *
     * @param msg 原始数据
     * @param dataConsumer 数据处理
     * @return WaterKeyValue
     */
    void decode(WaterMqttMessage msg,
                Consumer<WaterKeyValue> dataConsumer);


}
