package com.honeywell.fireiot.fire.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ： YingZhang
 * @Description: 每个分区 对应的Point点
 * @Date : Create in 2:43 PM 10/9/2018
 */
@Data
public class PartPoint {

    private PartPointKey partPointKey;

    private List<ModbusPoint> list = new ArrayList<>();

    public void addPoint(ModbusPoint modbusPoint){
        if(list == null){
            list = new ArrayList<>();
        }
        list.add(modbusPoint);
    }

    public PartPoint(PartPointKey partPointKey) {
        this.partPointKey = partPointKey;
    }
}
