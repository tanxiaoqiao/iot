package com.honeywell.fireiot.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * RedisMap工厂
 *
 * @Author: zhenzhong.wang
 * @Time: 2018/9/10 16:07
 */
@Component
public class RedisMapFactory {

    // 激活的profile
//    private static String profile;
    private static String defaultPrefix;

    private static RedisTemplate redisTemplate;

    @Value("${redis.key.defaultPrefix}")
    public void setDefaultPrefix(String defaultPrefix) {
        RedisMapFactory.defaultPrefix = defaultPrefix;
    }

    @Autowired
    public void setRedisTemplate (RedisTemplate redisTemplate) {
        RedisMapFactory.redisTemplate = redisTemplate;
    }

    /**
     * 获取RedisMap
     *
     * @param hashKey
     * @return
     */
    public static RedisMap getRedisMap(String hashKey) {
        RedisMap redisMap = new DefaultRedisMap(redisTemplate, defaultPrefix + ":" + hashKey);
        return redisMap;
    }

    /**
     * 创建RedisMap，允许指定key prefix
     *
     * @param hashKey
     * @param prefix
     * @return
     */
    public static RedisMap getRedisMap(String hashKey, String prefix) {
        RedisMap redisMap = new DefaultRedisMap(redisTemplate, prefix + ":" + hashKey);
        return redisMap;
    }
}
