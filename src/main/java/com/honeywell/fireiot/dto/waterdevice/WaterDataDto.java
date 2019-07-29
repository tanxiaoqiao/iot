package com.honeywell.fireiot.dto.waterdevice;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 4:25 PM 4/16/2019
 */
@Data
public class WaterDataDto {
    private Long id;

    private String eui;

    private String fieldName;

    private String value;

    private Long timestamp;

    //单位
    private String unitSymbol;
}
