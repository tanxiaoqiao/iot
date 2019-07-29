package com.honeywell.fireiot.redis;

/**
 * RedisMap操作接口
 *
 * @Author: zhenzhong.wang
 * @Date: 9/10/2018 3:45 PM
 */
public interface RedisMap {
    /**
     * 存储
     *
     * @param key
     * @param value
     */
    void put(String key, Object value);

    /**
     * 取值
     *
     * @param key
     * @return
     */
    Object get(String key);

    /**
     * 删除RedisMap中键值
     *
     * @param keys
     * @return
     */
    Long remove(Object... keys);

    /**
     * 删除RedisMap
     */
    public Long removeAll();
}
