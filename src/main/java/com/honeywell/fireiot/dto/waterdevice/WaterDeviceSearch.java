package com.honeywell.fireiot.dto.waterdevice;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 水系统设备查询VO
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/27 10:25 AM
 */
@Getter
@Setter
public class WaterDeviceSearch {
    @ApiModelProperty("设备类型")
    private String deviceType;
    @ApiModelProperty("楼栋")
    private String building;
    @ApiModelProperty("楼层")
    private String floor;
}
