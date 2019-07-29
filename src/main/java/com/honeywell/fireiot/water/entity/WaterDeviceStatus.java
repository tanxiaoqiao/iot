package com.honeywell.fireiot.water.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: xiaomingCao
 * @create: 1/20/2019
 */
@Data
public class WaterDeviceStatus implements Serializable {

    private int status;

    private long latestUploadTimestamp;

    private String eui;

}
