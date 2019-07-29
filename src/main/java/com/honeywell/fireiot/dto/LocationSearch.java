package com.honeywell.fireiot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description: 楼栋信息，包括楼栋和楼层
 * @Date : Create in 12:16 PM 1/2/2019
 */
@Data
public class LocationSearch {
    @ApiModelProperty("楼栋")
    private String building;
    @ApiModelProperty("楼层")
    private String floor;
}
