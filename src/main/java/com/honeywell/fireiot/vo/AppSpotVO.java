package com.honeywell.fireiot.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppSpotVO
 * @author: dexter
 * @create: 2019-03-19 10:23
 * @description:
 **/
@Data
public class AppSpotVO {
    /**
     * 点位id
     *
     */
    private Long id;

    /**
     * 点位名称
     *
     */
    private String name;

    /**
     * 安装位置全称
     *
     */
    private String fullName;

    /**
     * 安装位置id
     *
     */
    private Long locationId;

    @JsonProperty("taskList")
    private List<AppTaskFormVO> appTaskFormVOs;
}
