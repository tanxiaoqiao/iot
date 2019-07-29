package com.honeywell.fireiot.dto.waterdevice;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 单个设备详细信息，用户信息返回
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/28 1:09 PM
 */
@Data
public class WaterDeviceDetailInfoDto {
    private String deviceId;
    private String deviceType;
    private String deviceLabel;
    private String mapLocation;
    private String locationName;

    private String brandName; //品牌
    private String typeSpecification; //型号
    private Date dateOfPoduction; // 出厂日期
    private Date dateOfInstallation; //安装日期
    private Date  dateOfCommissioning; // 启用日期
    private String weight;//重量

    private Integer lifeTime;//设计寿命

    private String importance; //重要性

//    private String max;
//    private String min;
//    private String unit;
//    private String value;
//    private String deviceStatus;

    // 值数组，max,min,unit,value,status
    List<Map<String, Object>> values;
    private String deviceEUI;
    private String applyType;
    private String head_4;
    private String head_2;
    private String input;
    private String description;

}
