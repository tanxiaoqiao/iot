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
 * 温湿度传感器数据解码
 *
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Slf4j
public class TemperatureAndHumiditySensorDecoder implements WaterDecoder {


    private final int DATA_LENGTH = 5;



    /**
     * 将消息解析为温度，湿度，电压
     *
     * @param msg
     * @param dataConsumer
     * @return
     */
    @Override
    public void decode(WaterMqttMessage msg,
                       Consumer<WaterKeyValue> dataConsumer) {
        byte[] bytes = Base64.getDecoder().decode(msg.getData().getBytes());

        if(bytes.length != DATA_LENGTH){
            log.error("bad data: eui={}, data={}", msg.getDevEUI(), msg.getData());
            return;
        }

        double temperature = (double)(bytes[0] << 8  & 0xFF00 | bytes[1] & 0xFF) / 100;

        double humidity = (double)(bytes[2] << 8  & 0xFF00 | bytes[3] & 0xFF) / 100;

        int electricPressureAlarm = (bytes[4] >> 7) & 0x1;

        double electricPressure = ((double) ((bytes[4] & ~(1 << 7)) & 0xFF)) / 10;


        Map<String, String> data = new HashMap<>(16);

        data.put(TEMPERATURE, temperature + "");
        data.put(HUMIDITY, humidity + "");
        data.put(ELECTRIC_PRESSURE, electricPressure + "");
        data.put(ELECTRIC_PRESSURE_ALARM, electricPressureAlarm + "");

        WaterKeyValue keyValue = new WaterKeyValue();
        keyValue.setData(data);
        keyValue.setEui(msg.getDevEUI());
        keyValue.setTimestamp(msg.getTimestamp());
        dataConsumer.accept(keyValue);
    }
}
