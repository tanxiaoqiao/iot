package com.honeywell.fireiot.water.service;

import com.honeywell.fireiot.water.configuration.WaterProperties;
import com.honeywell.fireiot.water.constant.WaterDeviceStatusEnum;
import com.honeywell.fireiot.water.entity.WaterDeviceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaomingCao
 * @date: 2019/1/21
 */
@EnableConfigurationProperties(WaterProperties.class)
@Configuration
@Service
public class WaterDeviceStatusService {

    @Qualifier("waterDeviceStatusCache")
    @Autowired
    private RedisTemplate<String, WaterDeviceStatus> redisTemplate;

    @Autowired
    private WaterProperties waterProperties;


    /**
     * 更新设备状态
     *
     * @param eui
     * @param status
     * @param timestamp
     */
    public void updateStatus(String eui, WaterDeviceStatusEnum status, long timestamp){
        WaterDeviceStatus waterDeviceStatus = new WaterDeviceStatus();
        waterDeviceStatus.setStatus(status.getValue());
        waterDeviceStatus.setLatestUploadTimestamp(timestamp);
        waterDeviceStatus.setEui(eui);
        redisTemplate.opsForValue().set(eui, waterDeviceStatus, waterProperties.getStatusCacheTimeout(), TimeUnit.MINUTES);
    }


    /**
     * 获取设备状态
     *
     * @param eui
     * @return
     */
    public WaterDeviceStatus getStatus(String eui){
        WaterDeviceStatus waterDeviceStatus = redisTemplate.opsForValue().get(eui);
        if(Objects.isNull(waterDeviceStatus)){
            waterDeviceStatus = new WaterDeviceStatus();
            waterDeviceStatus.setStatus(WaterDeviceStatusEnum.OFFLINE.getValue());
            return waterDeviceStatus;
        }
        return waterDeviceStatus;
    }

}
