package com.honeywell.fireiot.dto;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class BusinessDeviceDto {

    private Long id;

    @NotEmpty(message = "设备编号不能为空")
    private String deviceNo;            // 设备编号
    private String deviceLabel;         // 设备标签
    private Long locationId;          // 位置ID
    private String locationDetail;      // 详细位置

    private DeviceTypeDto deviceType;   // 设备类型
    private SystemTypeDto systemType;   // 系统类型

    private Long deviceTypeId;// 设备类型Id

    private Long systemTypeId;// 设备类型Id

    private String deviceStatus; //设备状态

    private String brandName; //品牌

    private String typeSpecification; //型号

    private Date dateOfPoduction; // 出厂日期

    private Date dateOfInstallation; //安装日期

    private Date  dateOfCommissioning; // 启用日期

    private String weight;//重量

    private Integer lifeTime;//设计寿命

    private String importance; //重要性

    private String  description;//描述

    private String mapLocation;//地图位置
}
