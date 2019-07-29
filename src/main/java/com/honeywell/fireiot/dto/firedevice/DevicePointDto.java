package com.honeywell.fireiot.dto.firedevice;

import lombok.Data;

/**
 * @Author: Kayla, Ye
 * @Description: 用于生成数据
 * @Date:Created in 5:09 PM 12/14/2018
 */
@Data
public class DevicePointDto {

    private  String __id;

    private String network;// 网络号对应的点表

    private  String zone;//区号 （《= 9999）

    private String point;//点号（《=32）

    private String name;//中文地址

    private  String  loop;//回路号，固定为四位不足以0补足，前两位表示主机号，第三为卡号， 第四位槽号

    private String type;//设备类型

    private String htConfig; //需要填充字段

    public DevicePointDto(){

    }

    public DevicePointDto(String _id, String zone, String point, String name, String loop, String type, String network){
        this.__id = _id;
        this.loop = loop;
        this.zone = zone;
        this.point = point;
        this.network = network;
        this.name = name;
        this.type = type;
    }

}
