package com.honeywell.fireiot.water.schedule.bean;

import lombok.Data;

/**
 * @author: xiaomingCao
 * @date: 2019/2/18
 */
@Data
public class LoraApiGatewayStatus {

    private String gatewayEUI;

    private String code;

    private String timestamp;

    private String period;

    private String onlineTime;
}
