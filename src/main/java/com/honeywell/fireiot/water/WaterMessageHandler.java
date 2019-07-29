package com.honeywell.fireiot.water;

import com.alibaba.fastjson.JSON;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.entity.Location;
import com.honeywell.fireiot.entity.WaterDevice;
import com.honeywell.fireiot.integrationService.rabbitmq.RabbitmqProps;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.LocationService;
import com.honeywell.fireiot.service.WaterDeviceService;
import com.honeywell.fireiot.water.constant.WaterDeviceStatusEnum;
import com.honeywell.fireiot.water.entity.WaterEvent;
import com.honeywell.fireiot.water.mqtt.bean.WaterMqttMessage;
import com.honeywell.fireiot.water.mqtt.bean.WaterMqttProcessor;
import com.honeywell.fireiot.water.sensor.bean.WaterKeyValue;
import com.honeywell.fireiot.water.sensor.codec.WaterDecoder;
import com.honeywell.fireiot.water.sensor.codec.impl.*;
import com.honeywell.fireiot.water.service.WaterDataService;
import com.honeywell.fireiot.water.service.WaterDeviceStatusService;
import com.honeywell.fireiot.water.service.WaterEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.honeywell.fireiot.water.sensor.constant.WaterSensorEnum.*;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Order
@Configuration
@Slf4j
public class WaterMessageHandler {

    private static  Map<String, WaterDecoder> decoderHub;

    @Autowired
    private WaterDataService waterDataService;

    @Autowired
    private WaterEventService waterEventService;

    @Autowired
    private WaterDeviceService waterDeviceService;

    @Autowired
    private BusinessDeviceService businessDeviceService;

    @Autowired
    private LocationService locationService;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RabbitmqProps rabbitmqProps;

    @Autowired
    private WaterDeviceStatusService waterDeviceStatusService;


    /*
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String CACHE_KEY = "water_system_eui_device_type_cache";
    */


    static {
        decoderHub = new HashMap<>(16);
        decoderHub.put(WATER_SYS_TEMPERATURE_HUMIDITY.name(), new TemperatureAndHumiditySensorDecoder());
        decoderHub.put(WATER_SYS_WATER_IMMERSION.name(), new WaterImmersionSensorDecoder());
        decoderHub.put(WATER_SYS_PRESSURE_COLLECT.name(), new PressureCollectUnitDecoder());
        decoderHub.put(WATER_SYS_LIQUID_LEVEL.name(), new LiquidLevelSensorDecoder());
        decoderHub.put(WATER_SYS_BATTERY.name(), new BatteryCheckSensorDecoder());
        decoderHub.put(WATER_SYS_FLOWMETER.name(), new FlowmeterSensorDecoder());
    }







    /**
     * 处理水系统mqtt消息
     *
     * @return
     */
    @Bean
    @ServiceActivator(inputChannel = WaterMqttProcessor.INPUT)
    public MessageHandler handler() {
        return (Message<?> message) -> {
            WaterMqttMessage wmsg = (WaterMqttMessage) message.getPayload();
            WaterDecoder waterDecoder = getDecoder(wmsg.getDevEUI());
            if(waterDecoder == null){
                log.warn("unsupported decode device type: eui={}", wmsg.getDevEUI());
                return;
            }

            waterDeviceStatusService.updateStatus(wmsg.getDevEUI(), WaterDeviceStatusEnum.ONLINE, wmsg.getTimestamp());

            // 时间戳为MQTT接收时间, 其他时间认为不可靠时间
            long milli = message.getHeaders().getTimestamp();
            wmsg.setTimestamp(milli);
            // 解析
            try {
                waterDecoder.decode(wmsg,
                        (kv) -> {

                            // 持久s化
                            kv.setEui(wmsg.getDevEUI());
                            kv.setTimestamp(milli);
                            waterDataService.save(kv);

                            // 自定义上下限检查
                            // 该事件为服务端检查上下限产生事件
                            List<WaterEvent> eventList = waterEventService.checkAlarm(kv);


                            if (CollectionUtils.isEmpty(eventList)) {
                                return;
                            }

                            consumerEvent(eventList,  kv);

                        }
                );
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }


        };
    }


    /**
     * 消费事件
     *
     * @param events
     */
    public void consumerEvent(List<WaterEvent> events, WaterKeyValue kv){
        List<Map<String, Object>> bigScreenEvents = new ArrayList<>();

        List<Map<String, Object>> fireiotEvents = new ArrayList<>();

        Optional<WaterDevice> opt =
                waterDeviceService.findByEui(kv.getEui());
        if(!opt.isPresent()){
            return;
        }
        WaterDevice waterDevice = opt.get();

        BusinessDevice businessDevice =
                businessDeviceService.findByNo(waterDevice.getDeviceNo());

        if(Objects.isNull(businessDevice)){
            return;
        }

        events.forEach(e -> {
            Location location = locationService.getInfo(businessDevice.getLocationId());
            bigScreenEvents.add(transform2BigScreenEvent(kv, e, businessDevice, location));
            fireiotEvents.add(transform2FireIotEvent(e, businessDevice, location));
        });

        // 推送
        bigScreenEvents.forEach(map -> {
            String eventString = JSON.toJSONString(map);
            amqpTemplate.send(rabbitmqProps.getWaterExchange(),"",new org.springframework.amqp.core.Message(eventString.getBytes(),new MessageProperties()));
            log.info("水系统事件大屏推送：{}", eventString);
        });

    }





    /**
     * 水系统事件转为大屏事件
     *
     * @param waterEvent
     * @return
     */
    public Map<String, Object> transform2BigScreenEvent(WaterKeyValue keyValue,
                                                        WaterEvent waterEvent,
                                                        BusinessDevice businessDevice,
                                                        Location location){
        Map<String, Object> data = new HashMap<>(16);
        data.put("max", waterEvent.getMax());
        data.put("min", waterEvent.getMin());
        data.put("value", waterEvent.getValue());
        data.put("unit", waterEvent.getUnit());
        data.put("eventType", waterEvent.getDescription());
        data.put("eventStatus", waterEvent.getType());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(keyValue.getTimestamp());
        data.put("createDatetime", seconds);
        data.put("deviceLabel", businessDevice.getDeviceLabel());
        data.put("deviceId", businessDevice.getDeviceNo());
        data.put("deviceType", businessDevice.getDeviceType().getName());
        if(Objects.nonNull(location)){
            data.put("building", location.getBuilding());
            if(location.getFloor() != null){
                data.put("floor", location.getFloor());
            }else{
                data.put("floor", "");
            }
        }else{
            data.put("building", "");
            data.put("floor", "");
        }
        return data;
    }


    /**
     * 水系统事件转为fire iot事件
     *
     * @param waterEvent
     * @return
     */
    public Map<String, Object> transform2FireIotEvent(WaterEvent waterEvent,
                                                      BusinessDevice businessDevice,
                                                      Location location){
        Map<String, Object> data = new HashMap<>(16);
        data.put("deviceId", businessDevice.getDeviceNo());
        data.put("createDatetime", waterEvent.getStartTime());
        data.put("deviceLabel", businessDevice.getDeviceLabel());
        data.put("deviceType", businessDevice.getDeviceType().getName());
        data.put("eventType", waterEvent.getDescription());
        data.put("eventStatus", waterEvent.getType());
        if(Objects.nonNull(location)){
            data.put("building", location.getFullName());
            data.put("building", location.getName());
        }
        return data;
    }



    /**
     * 通过eui查询deviceType, 通过deviceType匹配解码器
     *
     * @param eui
     * @return
     */
    private WaterDecoder getDecoder(String eui) {
        /*
        ValueOperations ops = redisTemplate.opsForValue();

        WaterDeviceTypeCache cache = (WaterDeviceTypeCache) ops.get(CACHE_KEY);

        if(Objects.isNull(cache)){
            cache = new WaterDeviceTypeCache();
        }

        String type = cache.get(eui);

        if(Objects.isNull(type)){
            type = waterDeviceService.findByEui(eui)
                    .map(WaterDevice::getDeviceNo)
                    .map(businessDeviceService::findByNo)
                    .map(BusinessDevice::getDeviceType)
                    .map(DeviceType::getEName)
                    .map(String::toUpperCase)
                    .orElse(null);
            cache.set(eui, type);
            ops.set(CACHE_KEY, cache, 1, TimeUnit.HOURS);
        }
        return decoderHub.get(type);
        */
        return waterDeviceService.findByEui(eui)
                .map(WaterDevice::getDeviceNo)
                .map(businessDeviceService::findByNo)
                .map(BusinessDevice::getDeviceType)
                .map(DeviceType::getDescription)
                .map(String::toUpperCase)
                .map(decoderHub::get)
                .orElse(null);
    }

}
