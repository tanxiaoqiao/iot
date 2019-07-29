package com.honeywell.fireiot.dto.waterdevice;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 水系统信息封装类，用于接口返回数据封装
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/27 2:46 PM
 */
@Setter
@Getter
@ApiModel
public class WaterDeviceInfoDto {

    // 返回数据中加入查询条件
    private Object search;
    private List values;
}
