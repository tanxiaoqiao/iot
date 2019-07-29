package com.honeywell.fireiot.dto.firedevice;

import com.honeywell.fireiot.constant.FileImportAction;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 1:42 PM 1/3/2019
 */
@Data
public class FireDevicePointSearch {
    @ApiModelProperty("系统类型")
    private String SystemType = FileImportAction.FIRE_SYSTEM_NAME;
    @ApiModelProperty("楼栋")
    private String building;
    @ApiModelProperty("楼层")
    private String floor;
}
