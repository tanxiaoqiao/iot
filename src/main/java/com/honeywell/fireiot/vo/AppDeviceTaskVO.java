package com.honeywell.fireiot.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.honeywell.fireiot.dto.FormElementDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppDeviceTaskVO
 * @author: dexter
 * @create: 2019-04-26 15:50
 * @description:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppDeviceTaskVO {

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    @JsonProperty("formElementList")
    List<FormElementDto> formElements;
}
