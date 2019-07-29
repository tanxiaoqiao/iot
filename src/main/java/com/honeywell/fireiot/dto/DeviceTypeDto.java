package com.honeywell.fireiot.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class DeviceTypeDto {

    private Long id;

    @NotEmpty(message = "设备类型名称不能为空")
    private String name;
    private String description;

    private Long systemTypeId;

    private List<EventTypeDto> eventTypes;

}