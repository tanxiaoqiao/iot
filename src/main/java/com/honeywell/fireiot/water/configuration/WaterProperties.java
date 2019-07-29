package com.honeywell.fireiot.water.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: xiaomingCao
 * @date: 2019/1/21
 */
@Data
@ConfigurationProperties("water")
public class WaterProperties {


    private long statusCacheTimeout = 30;



}
