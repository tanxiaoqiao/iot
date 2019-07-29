package com.honeywell.fireiot.water.sensor.codec.impl;

import com.honeywell.fireiot.water.mqtt.bean.WaterMqttMessage;
import com.honeywell.fireiot.water.sensor.bean.WaterKeyValue;
import com.honeywell.fireiot.water.sensor.codec.WaterDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.honeywell.fireiot.water.sensor.constant.WaterSensorItems.*;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Slf4j
public class LiquidLevelSensorDecoder implements WaterDecoder {


    private final int DATA_LENGTH = 3;


    /**
     * 将消息体解码为指定类型，事件处理，数据处理
     *
     * @param msg           原始数据
     * @param dataConsumer  数据处理
     * @return WaterKeyValue
     */
    @Override
    public void decode(WaterMqttMessage msg,
                       Consumer<WaterKeyValue> dataConsumer) {

        byte[] bytes = Base64.getDecoder().decode(msg.getData().getBytes());

        if(bytes.length != DATA_LENGTH){
            log.error("bad data: eui={}, data={}", msg.getDevEUI(), msg.getData());
            return;
        }

        int liquidLevel = (bytes[0] << 8  & 0xFF00 | bytes[1] & 0xFF);

        int electricPressureAlarm = (bytes[2] >> 7) & 0x1 ;

        double electricPressure = ((double) ((bytes[2] & ~(1 << 7)) & 0xFF)) / 10;



        Map<String, String> data = new HashMap<>(16);


        data.put(LIQUID_LEVEL, liquidLevel + "");

        data.put(ELECTRIC_PRESSURE, electricPressure + "");

        data.put(ELECTRIC_PRESSURE_ALARM, electricPressureAlarm + "");

        WaterKeyValue keyValue = new WaterKeyValue();
        keyValue.setData(data);
        keyValue.setEui(msg.getDevEUI());
        keyValue.setTimestamp(msg.getTimestamp());
        dataConsumer.accept(keyValue);
    }
}
