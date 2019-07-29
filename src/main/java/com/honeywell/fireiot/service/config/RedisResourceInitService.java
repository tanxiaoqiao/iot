package com.honeywell.fireiot.service.config;

import com.honeywell.fireiot.constant.RedisConstant;
import com.honeywell.fireiot.entity.Resource;
import com.honeywell.fireiot.entity.Role;
import com.honeywell.fireiot.entity.RoleResourceRel;
import com.honeywell.fireiot.redis.RedisMap;
import com.honeywell.fireiot.redis.RedisMapFactory;
import com.honeywell.fireiot.service.RoleService;
import com.honeywell.fireiot.water.entity.WaterField;
import com.honeywell.fireiot.water.repository.WaterFieldRepository;
import com.honeywell.fireiot.water.service.WaterFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Redis资源初始化
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/4/11 9:44 AM
 */
@Service
@Slf4j
public class RedisResourceInitService {

    ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    RoleService roleService;
    @Autowired
    WaterFieldRepository waterFieldRepository;

    private static RedisMap securityMap;
    private static RedisMap waterMap;
    private static RedisMapFactory redisFactory;

    @Autowired
    public void setRedisFactory(RedisMapFactory redisFactory) {
        RedisResourceInitService.redisFactory = redisFactory;
        RedisResourceInitService.securityMap = RedisMapFactory.getRedisMap(RedisConstant.HASH_KEY_SECURITY);
        RedisResourceInitService.waterMap = RedisMapFactory.getRedisMap(RedisConstant.HASH_KEY_WATER);
    }

    public void initAll() {
        refreshResource();
        loadWaterField();
    }

    public void refreshResource() {
        executor.execute(new Thread(() -> {
            try {
                // 延时2秒，为角色资源的DB更新提供足够的时间
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadResource();
        }));
    }

    public void loadResource() {
        /**
         * 应当是资源(格式：method,api)为key， 权限为value。 资源为URL-Method的Map， 权限就是那些以ROLE_为前缀的角色。
         * 一个资源可以由多个权限来访问。
         */
        Map<String, Collection<Object>> resourceMap = new HashMap<String, Collection<Object>>();

        /*
         * 在Web服务器启动时，提取系统中的所有权限。
         */
        List<Role> authorityList = roleService.findAll();

        /*
         * 取得所有权限名
         */
        for (Role auth : authorityList) {
            if (auth.getRoleResourceRels() == null) {
                continue;
            }

            String authName = auth.getSystemType() + "-" + auth.getName();
            /*
             * 然后，取得资源名：根据权限名 取得 资源名
             */
            for (RoleResourceRel rel : auth.getRoleResourceRels()) {
                /*
                 * 判断资源文件和权限的对应关系，如果已经存在相关的资源url，则要限通过该url为key提取出权集合，将权限增加到权限集合中
                 */
                Resource resource = rel.getResource();
                String resKey = (resource.getMethod() == null ? "" : resource.getMethod()) + "," + resource.getUrl();
                if (resourceMap.containsKey(resKey)) {
                    Collection<Object> value = resourceMap.get(resKey);
                    value.add(authName);
                } else {
                    Collection<Object> authNames = new ArrayList<Object>();
                    authNames.add(authName);
                    resourceMap.put(resKey, authNames);
                }
            }
        }
        /*
         * 更新redis缓存
         */
        securityMap.remove(RedisConstant.HASH_KEY_SECURITY_RESOURCE);
        securityMap.put(RedisConstant.HASH_KEY_SECURITY_RESOURCE, resourceMap);
        log.info("资源配置已更新");
    }

    public void loadWaterField() {
        Iterable<WaterField> waterFields = waterFieldRepository.findAll();

        Map<String, List<WaterField>> fieldMap = new HashMap<>();
        waterFields.forEach(field -> {
            List<WaterField> fieldList;
            if (fieldMap.get(field.getDeviceNo()) == null) {
                fieldList = new ArrayList<>();
                fieldMap.put(field.getEui(), fieldList);
            } else {
                fieldList = fieldMap.get(field.getDeviceNo());
            }

            fieldList.add(field);
        });

        waterMap.put(RedisConstant.HASH_KEY_WATER_FIELD, fieldMap);
        log.info("water field info 已加载");
    }
}
