package com.honeywell.fireiot.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 2:07 PM 12/29/2018
 */
@Data
public class BaseSearch {
    @ApiModelProperty(value="pageIndex(defult：1)")
    private int pi = 1;
    @ApiModelProperty(value="pageSize（defult：10）")
    private int ps = 10;
}
