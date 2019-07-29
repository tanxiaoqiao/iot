package com.honeywell.fireiot.fire.bo;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 9:44 AM 9/27/2018
 */
@Data
public class ModbusPoint {

    // 写入时需要PointId
//    private Long pointId;

    private Long fireDeviceId;
    // 中文状态：火警/故障等
    private String cName;
    // 数据类型
    private String dataType;
    // 分区
    private int slaveId;
    // 功能码
    private int functionCode;
    // 实际寄存器地址
    private int realAddress;
    // 仅dataType是bit时需要设置
    private int bitNum;

    // dataType对应的字节数
    private int byteCount;
    // dataType对应的寄存器数量
    private int registerCount;

}
