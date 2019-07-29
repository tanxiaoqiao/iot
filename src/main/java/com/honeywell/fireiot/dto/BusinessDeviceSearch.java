package com.honeywell.fireiot.dto;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description: 业务设备查询
 * @Date : Create in 7:33 PM 1/2/2019
 */
@Data
public class BusinessDeviceSearch extends BaseSearch{
    // 系统类型
    private String systemType;

}
