package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 12:15 PM 1/2/2019
 */
@Data
public class FireDeviceInfo {
    private Object search;

    private List<DeviceEventDto> values = new ArrayList<>();

    public void addDeviceEventDto(DeviceEventDto deviceEventDto){
        values.add(deviceEventDto);
    }

    public void removeDeviceEventDto(DeviceEventDto deviceEventDto){
        values.remove(deviceEventDto);
    }
}
