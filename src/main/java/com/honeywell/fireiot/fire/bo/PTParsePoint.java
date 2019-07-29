package com.honeywell.fireiot.fire.bo;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description: 每次modbus读取分区时的Point
 * @Date : Create in 4:49 PM 10/9/2018
 */
@Data
public class PTParsePoint {
    //value怎么转换
    private String dataType;

    // 读取bit的起始地址
    private int byteStart;
    // 读取几个bit
    private int byteAmount;
    // bit的哪一位
    private int bitNum;

    // 存放值
    private String value;

    // 转出用
    private Long fireDeviceId;
    // 转出用
    private String Cname;

    public PTParsePoint(String dataType, Long fireDeviceId, String cname) {
        this.dataType = dataType;
        this.fireDeviceId = fireDeviceId;
        Cname = cname;
    }
}
