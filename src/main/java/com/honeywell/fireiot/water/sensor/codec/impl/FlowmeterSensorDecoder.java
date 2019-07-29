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
public class FlowmeterSensorDecoder implements WaterDecoder {


    /**
     * 将消息体解码为指定类型，事件处理，数据处理
     *
     * @param msg          原始数据
     * @param dataConsumer 数据处理
     * @return WaterKeyValue
     */
    @Override
    public void decode(WaterMqttMessage msg,
                       Consumer<WaterKeyValue> dataConsumer) {

        byte[] ss = Base64.getDecoder().decode(msg.getData().getBytes());

        byte[] nn = new byte[ss.length-8];

        int j = 0;

        for (int i = 2; i < (ss.length-6); i++) {
            nn[j] = ss[i];
            j++;
        }

        String flowStr = new String(nn);

        double flow = 0.0;
        try {
            flow = Double.parseDouble(flowStr);
        }catch (Exception e){
            log.error("流量计解析有误: eui="+msg.getDevEUI(), e);
        }

        WaterKeyValue kv = new WaterKeyValue();
        kv.setTimestamp(msg.getTimestamp());
        kv.setEui(msg.getDevEUI());
        Map<String, String> data = new HashMap<>(16);
        data.put(WaterSensorItems.FLOW, flow + "");
        kv.setData(data);
        dataConsumer.accept(kv);
    }
}
