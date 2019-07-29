package com.honeywell.fireiot.fire.bo;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 4:23 PM 10/15/2018
 */
@Data
public class MTResponsePoint {
    private Long fireDeviceId;
    // 中文状态：火警/故障等
    private String cName;
    // "true":发生事件   "false": (事件恢复)删除
    private String value;
}
