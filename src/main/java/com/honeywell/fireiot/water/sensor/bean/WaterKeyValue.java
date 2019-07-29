package com.honeywell.fireiot.water.sensor.bean;

import lombok.Data;

import java.util.Map;

/**
 * @author: xiaomingCao
 * @date: 2018/12/26
 */
@Data
public class WaterKeyValue {

    private String eui;

    private Long timestamp;

    private Map<String, String> data;
}
