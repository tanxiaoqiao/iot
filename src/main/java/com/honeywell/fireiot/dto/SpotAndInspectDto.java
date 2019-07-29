package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName SpotAndInspectDto
 * @Description 点位与巡检表单内容
 * @Author Monica Z
 * @Date 2019/1/21 14:55
 */
@Data
public class SpotAndInspectDto {
    /**
     * 点位id
     */
    private long spotId;
    /**
     * 巡检内容
     */
    private List<FormStructureDto> formList;
}
