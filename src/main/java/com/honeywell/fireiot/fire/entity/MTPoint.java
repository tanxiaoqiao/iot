package com.honeywell.fireiot.fire.entity;

import com.honeywell.fireiot.entity.BaseEntity;
import com.honeywell.fireiot.entity.FireDevice;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @Author ： YingZhang
 * @Description: 解析的设备属性
 * @Date : Create in 5:32 PM 12/14/2018
 */
@Data
@Entity
@Table(name = "mt_point")
public class MTPoint extends BaseEntity<MTPoint> {
    // 设备
    @ManyToOne
    private FireDevice fireDevice;
    // 设备属性 ：AlM（火警）/ FLT（故障）/DIS（隔离） ACT（激活）
    private String cName;

    private String eName;
    // 寄存器地址
    private int address;
    // 第几位（0~15）
    private int key;

    // 需要转换的数据类型
    private String dataType = "bit";
    // 功能码 1 2 3 4
    private int functionCode = 3;
    // slaveId
    private int slaveId = 1;

    public void setcName() {
        switch (eName) {
            case "ALM":
                cName = "火警";
                break;
            case "FLT":
                cName = "故障";
                break;
            case "DIS":
                cName = "隔离";
                break;
            case "ACT":
                cName = "激活";
                break;
            default:
        }
    }
}
