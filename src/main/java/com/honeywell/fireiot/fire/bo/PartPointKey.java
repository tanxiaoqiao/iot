package com.honeywell.fireiot.fire.bo;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description: 分区号 和 功能码 确定分区
 * @Date : Create in 10:19 AM 10/12/2018
 */
@Data
public class PartPointKey {

    // 分区号（主机地址）
    private Integer slaveId ;
    // 功能码
    private Integer functionCode;

    public PartPointKey(Integer slaveId, Integer functionCode) {
        this.slaveId = slaveId;
        this.functionCode = functionCode;
    }
}
