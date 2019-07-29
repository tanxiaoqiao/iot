package com.honeywell.fireiot.entity;


import com.honeywell.fireiot.dto.BusinessDeviceDto;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Data
public class WorkorderDevice implements Serializable {

    private Long id;
    private String deviceNo;
    private String locationDetail;
    private String deviceLabel;


    public static WorkorderDevice toDto(BusinessDeviceDto wo) {
        WorkorderDevice wd = new WorkorderDevice();
        BeanUtils.copyProperties(wo, wd);
        return wd;
    }


}
