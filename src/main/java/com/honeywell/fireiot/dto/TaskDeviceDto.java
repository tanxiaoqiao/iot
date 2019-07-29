package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName TaskDeviceDto
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/17 15:00
 */
@Data
public class TaskDeviceDto {
    /**
     * 点位id
     */
    private long id;
    /**
     * 任务id
     */
    private long taskId;
    /**
     * 设备id
     */
    private List<Long> deviceId;
    /**
     * 设备
     */
    private List<BusinessDeviceDto> devices;
}
