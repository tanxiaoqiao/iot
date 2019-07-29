package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author ： YingZhang
 * @Description:  设备的所有信息，包括楼栋楼层，设备类型，系统类型，设备状态
 * @Date : Create in 7:59 PM 1/2/2019
 */
@Data
public class BusinessLocationDto {

    private String deviceId;

    private String deviceLabel;     // 设备标签

    private String building;        // 楼栋

    private String floor;       // 楼层

    private String deviceType;      // 设备类型

    private String systemType;      // 系统类型

    private String deviceStatus; //设备状态（这个是台账里面的设备状态，不是设备的状态）

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


    private List<String> deviceEventStatus; // 设备事件状态
    // 区域（A/B/C/D）
    private String network;
    // 回路号
    private String loop;
    // 区号
    private String zone;
    // 点号
    private String point;
}
