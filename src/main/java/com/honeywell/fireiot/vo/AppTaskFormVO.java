package com.honeywell.fireiot.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppTaskFormVO
 * @author: dexter
 * @create: 2019-03-19 10:28
 * @description:
 **/
@Data
public class AppTaskFormVO {

    /**
     * 任务id
     *
     */
    private Long id;

    /**
     * 任务名称
     *
     */
    private String name;

    /**
     * 任务内容
     *
     */
    private String content;

    /**
     * 综合表单
     *
     */
    @JsonProperty("generalTask")
    private AppGeneralTaskVO appGeneralTaskVO;

    /**
     * 设备表单
     *
     */
    @JsonProperty("deviceTaskList")
    private List<AppDeviceTaskVO> appDeviceTaskVOList;
}
