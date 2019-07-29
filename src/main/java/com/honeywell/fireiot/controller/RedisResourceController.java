package com.honeywell.fireiot.controller;

import com.honeywell.fireiot.service.config.RedisResourceInitService;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置接口
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/21 1:54 PM
 */
@RestController
@RequestMapping
@Slf4j
@Api(tags = "系统配置")
public class RedisResourceController {

    @Autowired
    RedisResourceInitService redisResourceService;

    @PostMapping("/api/redis/resource/init")
    public ResponseObject init() {
        redisResourceService.initAll();
        return ResponseObject.success("OK");
    }
}
