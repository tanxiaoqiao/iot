package com.honeywell.fireiot.entity;

import com.honeywell.fireiot.fire.entity.MTGateway;
import com.honeywell.fireiot.fire.entity.MTPoint;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

/**
 * @Author ： YingZhang
 * @Description: ModbusTcp设备
 * @Date : Create in 4:32 PM 12/14/2018
 */
@Data
@Entity
@Table(name = "fire_device")
public class FireDevice extends BaseEntity<FireDevice> {

    // 设备台账共有
    private String businessDeviceNo;

    // 设备不会有协议解析地址，有可能多个地址对应一个设备

    // 区域（A/B/C/D）
    private String network;
    // 回路号
    private String loop;
    // 区号
    private String zone;
    // 点号
    private String point;

    // 网关号
    @ManyToOne
    private MTGateway mtGateway;

//    @ManyToOne
//    private Location location;

//    @ManyToOne
//    private DeviceType deviceType;  // 设备类型

    // 设备状态(多种状态)
    private String status = "";

    @OneToMany(fetch= FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="fireDevice" ,orphanRemoval = true)
    private List<MTPoint>  mtPoints;

    /**
     * 设备可存在多种状态，增加设备状态
     *
     * @param newStatus 新状态
     */
    public void addStatus(String newStatus) {
        if ("".equals(status)) {
            status = newStatus;
        } else if(status.contains(newStatus)){
            // 如果包含了就不发生改变
        } else {
            status += "," + newStatus;
        }
    }

    /**
     * 移除某种设备状态
     *
     * @param aStatus
     */
    public void removeStatus(String aStatus) {
        if(status == ""){
            return ;
        }else{
            String newAStatus = ","+aStatus;
            String newBStatus = aStatus + ",";
            if(status.contains(newAStatus)){
                // 移除 ,aStatus
                status = status.replace(newAStatus,"");
            }else if(status.contains(newBStatus)){
                // 移除 aStatus,
                status = status.replace(newBStatus,"");
            }else if(status.contains(aStatus)){
                // 移除 aStatus
                status = status.replace(aStatus,"");
            }
        }
    }

    /**
     * 将字符串转 换成字符数组 再转换成List
     * @return
     */
    public List<String> getStatus(){
        if("".equals(status)){
            return null;
        }else{
            return Arrays.asList(status.split(","));
        }
    }

    /**
     * 将List 转换成字符串
     * @param statusList
     */
    public void setStatus(List<String> statusList){
        StringBuffer stringBuffer = new StringBuffer();
        statusList.forEach(s -> {
            if(StringUtils.isNotBlank(s)){
                stringBuffer.append(",").append(s);
            }
        });
        status = stringBuffer.toString().substring(1);
    }

    public boolean contains(String aStatus){
        // 去除空字符串和null
        if(StringUtils.isBlank(aStatus)){
            return false;
        }
        if(status.contains(aStatus)){
            return true;
        }else{
            return false;
        }
    }

}
