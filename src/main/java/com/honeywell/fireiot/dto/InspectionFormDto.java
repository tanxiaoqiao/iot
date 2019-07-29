package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName InspectionFormDto
 * @Description 巡检表单内容提交
 * @Author Monica Z
 * @Date 2019/1/21 14:05
 */
@Data
public class InspectionFormDto {
    /**
     * 巡检计划id
     */
    private long patrolId;
    /**
     * 巡检内容
     */
    private List<SpotAndInspectDto> list;

}
