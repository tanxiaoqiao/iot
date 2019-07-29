package com.honeywell.fireiot.water.configuration;

import com.honeywell.fireiot.water.entity.WaterDeviceStatus;
import com.honeywell.fireiot.water.util.WaterRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author: xiaomingCao
 * @create: 1/20/2019
 */
@Configuration
public class RedisConfiguration {

    @Bean(name = "waterDeviceStatusCache")
    public RedisTemplate<String, WaterDeviceStatus> waterDeviceStausRedisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, WaterDeviceStatus> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new WaterRedisSerializer());
        return template;
    }
}
