package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName TaskFormDto
 * @Description TODO
 * @Author Monica Z
 * @Date 2019/1/17 13:41
 */
@Data
public class TaskFormDto {
    private long taskId;
    /**
     * 巡检类型
     * 0 综合巡检
     * 1 设备巡检
     */
    private int type;
    /**
     * 关联设备id
     */
    private List<Long> deviceIds;
    /*
       巡检模版id
     */
    private List<Long> templateIds;
    /*
     巡检表单 -表单项目
      */
    private FormElementDto formElementDto;

    private List<Long> elementIds;
}
