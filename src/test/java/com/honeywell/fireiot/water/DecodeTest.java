package com.honeywell.fireiot.water;

import com.honeywell.fireiot.water.mqtt.bean.WaterMqttMessage;
import com.honeywell.fireiot.water.sensor.codec.impl.BatteryCheckSensorDecoder;
import com.honeywell.fireiot.water.sensor.codec.impl.FlowmeterSensorDecoder;
import com.honeywell.fireiot.water.sensor.codec.impl.TemperatureAndHumiditySensorDecoder;
import com.honeywell.fireiot.water.sensor.constant.WaterSensorItems;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static com.honeywell.fireiot.water.sensor.constant.WaterSensorItems.BATTERY_TEMPERATURE;
import static com.honeywell.fireiot.water.sensor.constant.WaterSensorItems.BATTERY_TEMPERATURE_ALARM;
import static com.honeywell.fireiot.water.sensor.constant.WaterSensorItems.ELECTRIC_PRESSURE;
import static com.honeywell.fireiot.water.sensor.constant.WaterSensorItems.HUMIDITY;
import static com.honeywell.fireiot.water.sensor.constant.WaterSensorItems.INTERNAL_RESISTANCE;
import static com.honeywell.fireiot.water.sensor.constant.WaterSensorItems.INTERNAL_RESISTANCE_ALARM;
import static com.honeywell.fireiot.water.sensor.constant.WaterSensorItems.TEMPERATURE;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
public class DecodeTest {




    @Test
    public void calcTest(){
        byte b1 = (byte)0x21;
        byte b2 = (byte)0x9F;
        int f1 = (b1 >> 7) & 0x1;
        double p1 = (double) ((b1 & ~(1 << 7)) & 0xFF) / 10;
        int f2 = (b2 >> 7) & 0x1;
        double p2 = (double) ((b2 & ~(1 << 7)) & 0xFF) / 10;
        Assert.assertArrayEquals(new int[]{0, 1}, new int[]{f1, f2});
        Assert.assertArrayEquals(new Double[]{3.3, 3.1}, new Double[]{p1, p2});
    }


    /**
     * 流量计解码
     */
    @Test
    public void flowTest(){
        WaterMqttMessage msg = getMsg("AwMrMC4wMDAwMDBFKzAwbTMvcw0K");
        FlowmeterSensorDecoder decoder = new FlowmeterSensorDecoder();
        decoder.decode(msg, kv ->
            Assert.assertEquals(0.0, Double.parseDouble(kv.getData().get(WaterSensorItems.FLOW)), 0.000)
        );
    }

    /**
     * 温湿度传感器数据解码
     */
    @Test
    public void temperatureAndHumiditySensorDecoderTest(){
        TemperatureAndHumiditySensorDecoder decoder = new TemperatureAndHumiditySensorDecoder();
        WaterMqttMessage msg = getMsg("CEgYsCQ=");

        decoder.decode(msg,
                (kv) ->
                        Assert.assertArrayEquals(
                                new Double[]{21.2, 63.2, 3.6},
                                new Double[]{
                                        Double.parseDouble(kv.getData().get(TEMPERATURE)),
                                        Double.parseDouble(kv.getData().get(HUMIDITY)),
                                        Double.parseDouble(kv.getData().get(ELECTRIC_PRESSURE))
                                })
        );
    }


    /**
     * 电池检测数据解码
     */
    @Test
    public void BatteryCheckDecoderTest(){
        BatteryCheckSensorDecoder decoder = new BatteryCheckSensorDecoder();
        decoder.decode(getMsg("AwMBAwwyxgD3AAAAAAAAAADqSA=="), (kv) -> {
            Map<String, String> data = kv.getData();
            Assert.assertArrayEquals(
                    new Double[]{12.998, 0d, 0d, 0d, 24.7},
                    new Double[]{
                            Double.parseDouble(data.get(ELECTRIC_PRESSURE)),
                            Double.parseDouble(data.get(BATTERY_TEMPERATURE_ALARM)),
                            Double.parseDouble(data.get(INTERNAL_RESISTANCE_ALARM)),
                            Double.parseDouble(data.get(INTERNAL_RESISTANCE)),
                            Double.parseDouble(data.get(BATTERY_TEMPERATURE))
                    }
            );
        });
    }



    /**
     * get mock data
     *
     * @param data
     * @return
     */
    private WaterMqttMessage getMsg(String data){
        WaterMqttMessage msg = new WaterMqttMessage();
        msg.setData(data);
        msg.setTimestamp(System.currentTimeMillis());
        msg.setDevEUI("");
        return msg;
    }


}
