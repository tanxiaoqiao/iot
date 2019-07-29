package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppSpotDTO
 * @author: dexter
 * @create: 2019-04-04 13:40
 * @description:
 **/
@Data
public class AppSpotDTO {

    /**
     * 点位id
     *
     */
    private Long spotId;

    /**
     * 用于统计点位正常、异常的个数，填写到Patrol表
     * 0 - 异常
     * 1 - 正常
     *
     */
    private Integer spotStatus;

    /**
     * 任务列表
     *
     */
    private List<AppTaskDTO> taskList;

    /**
     * 上报故障工单列表
     */
    private List<AppFaultDTO> faultList;
}
