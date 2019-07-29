package com.honeywell.fireiot.water.sensor.codec.impl;

import com.honeywell.fireiot.water.mqtt.bean.WaterMqttMessage;
import com.honeywell.fireiot.water.sensor.bean.WaterKeyValue;
import com.honeywell.fireiot.water.sensor.codec.WaterDecoder;
import com.honeywell.fireiot.water.sensor.constant.WaterSensorItems;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author: xiaomingCao
 * @date: 2019/1/3
 */
@Slf4j
public class BatteryCheckSensorDecoder implements WaterDecoder {


    private final int TA_DATA_LENGTH = 19;

    private final int TC_DATA_LENGTH = 13;

    private final int TA_TEST_RESPONSE_LENGTH = 10;


    /**
     * 将消息体解码为指定类型，事件处理，数据处理
     *
     * @param msg          原始数据
     * @param dataConsumer 数据处理
     * @return WaterKeyValue
     */
    @Override
    public void decode(WaterMqttMessage msg, Consumer<WaterKeyValue> dataConsumer) {

        byte[] bytes = Base64.getDecoder().decode(msg.getData().getBytes());

        if(TA_TEST_RESPONSE_LENGTH == bytes.length){
            log.info("TA test cmd response: {}", msg.getData());
        }


        if(TA_DATA_LENGTH == bytes.length){

            // 电压
            double electricPressure = ((double)(bytes[5] << 8  & 0xFF00 | bytes[6] & 0xFF))/1000;

            // 温度高位符号
            int p = (bytes[7] >> 7) & 0x1;

            // 温度
            double batteryTemperature = ((double)((bytes[7] & ~(1 << 7))<< 8 & 0xFF00 | bytes[8] & 0xFF))/10;

            if(p == 1){
                batteryTemperature = -1 * batteryTemperature;
            }

            // 单体内阻
            int  internalResistance = (bytes[9] << 8  & 0xFF00 | bytes[10] & 0xFF);

            // 电池温度告警状态
            int batteryTemperatureAlarm = (bytes[13] << 8  & 0xFF00 | bytes[14] & 0xFF);

            // 内阻告警状态
            int internalResistanceAlarm = (bytes[15] << 8  & 0xFF00 | bytes[16] & 0xFF);

            WaterKeyValue kv = new WaterKeyValue();
            kv.setTimestamp(msg.getTimestamp());
            kv.setEui(msg.getDevEUI());

            Map<String, String> data = new HashMap<>(16);
            data.put(WaterSensorItems.ELECTRIC_PRESSURE, electricPressure + "");
            data.put(WaterSensorItems.BATTERY_TEMPERATURE, batteryTemperature + "");
            data.put(WaterSensorItems.BATTERY_TEMPERATURE_ALARM, batteryTemperatureAlarm + "");
            data.put(WaterSensorItems.INTERNAL_RESISTANCE, internalResistance + "");
            data.put(WaterSensorItems.INTERNAL_RESISTANCE_ALARM, internalResistanceAlarm + "");
            kv.setData(data);
            dataConsumer.accept(kv);
            return;
        }


        if(TC_DATA_LENGTH == bytes.length){

            double electricCurrent = ((double)(bytes[5] << 8  & 0xFF00 | bytes[6] & 0xFF))/10;

            // 温度1高位符号
            int p1 = (bytes[7] >> 7) & 0x1;

            // 温度1
            double temperature1 = ((double)((bytes[7] & ~(1 << 7))<< 8 & 0xFF00 | bytes[8] & 0xFF))/10;

            if(p1 == 1){
                temperature1 = -1 * temperature1;
            }

            // 温度2高位符号
            int p2 = (bytes[9] >> 7) & 0x1;

            // 温度2
            double temperature2 = ((double)((bytes[9] & ~(1 << 7))<< 8 & 0xFF00 | bytes[10] & 0xFF))/10;

            if(p2 == 1){
                temperature2 = -1 * temperature2;
            }

            WaterKeyValue kv = new WaterKeyValue();
            kv.setEui(msg.getDevEUI());
            kv.setTimestamp(msg.getTimestamp());

            Map<String, String> data = new HashMap<>(16);
            data.put(WaterSensorItems.ELECTRIC_CURRENT, electricCurrent + "");
            data.put(WaterSensorItems.ENV_TEMPERATURE_1 , temperature1 + "");
            data.put(WaterSensorItems.ENV_TEMPERATURE_2, temperature2 + "");
            kv.setData(data);
            dataConsumer.accept(kv);
            return;
        }


        log.warn("unsupported bytes length");

    }
}
