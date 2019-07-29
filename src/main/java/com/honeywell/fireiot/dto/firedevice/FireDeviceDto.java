package com.honeywell.fireiot.dto.firedevice;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:59 PM 12/17/2018
 */
@Data
public class FireDeviceDto {

// ------------- FireDevice -----------------------
    private Long id;

    private String gatewayNo;//网关号

    // 设备当前状态
    private List<String> statuss =  new ArrayList<>();

    private String businessDeviceNo;
    // 区域（A/B/C/D）
    private String network;
    // 回路号
    private String loop;
    // 区号
    private String zone;
    // 点号
    private String point;


    public FireDeviceDto(){

    }

    public FireDeviceDto(String zone, String point,  String network,String loop, String businessDeviceNo, String gatewayNo){
        this.zone = zone;
        this.point = point;
        this.loop = loop;
        this.network = network;
        this.businessDeviceNo = businessDeviceNo;
        this.gatewayNo = gatewayNo;
    }
}
