package com.honeywell.fireiot.fire.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ： YingZhang
 * @Description: 每个modbus读取的 起始地址和 寄存器数量
 * @Date : Create in 4:45 PM 10/9/2018
 */
@Data
public class PTParse {
    // 起始地址
    private int addressStart;
    // 间隔
    private int interval;

    // 分区号（主机地址）
    private int slaveId ;
    // 功能码
    private int functionCode;

    private List<PTParsePoint> list = new ArrayList<>();

    public void addPoint(PTParsePoint ptParsePoint){
        if(list == null){
            list = new ArrayList<>();
        }
        list.add(ptParsePoint);
    }

    public PTParse(int slaveId, int functionCode) {
        this.slaveId = slaveId;
        this.functionCode = functionCode;
    }


    @Override
    public String toString() {
        return "PTParse{" +
                "addressStart=" + addressStart +
                ", interval=" + interval +
                ", list=" + list +
                '}';
    }
}
