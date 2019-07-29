package com.honeywell.fireiot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppPatrolVO
 * @author: dexter
 * @create: 2019-03-19 09:18
 * @description:
 **/
@Data
public class AppPatrolVO {

    /**
     * 巡检id
     *
     */
    private Long patrolId;

    /**
     * 巡检名称
     *
     */
    private String name;

    /**
     * 巡检状态
     * 0未巡检 1进行中 2 已完成 3延期完成 4未开始 5补检
     *
     */
    private Integer status;

    /**
     * 工作组id列表
     *
     */
    private List<String> workTeamIds;

    /**
     * 巡检计划id
     *
     */
    private Long pollingId;

    /**
     *  预计开始时间
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp preStartTime;

    /**
     * 预计结束时间
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp preEndTime;

    /**
     * 实际开始时间
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp actStartTime;

    /**
     * 实际结束时间
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp actEndTime;

    @JsonProperty("spotList")
    private List<AppSpotVO> appSpotVOs;

}
