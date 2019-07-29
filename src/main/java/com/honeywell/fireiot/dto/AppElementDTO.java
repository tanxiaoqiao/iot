package com.honeywell.fireiot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: fire-foxconn-back-polling
 * @name: AppElementDTO
 * @author: dexter
 * @create: 2019-04-04 09:27
 * @description:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppElementDTO {

    /**
     * 巡检内容名称
     */
    @JsonProperty("elementKey")
    private String name;

    /**
     * 巡检结果
     */
    @JsonProperty("elementValue")
    private String value;

    /**
     * 巡检内容状态
     * 0 - 异常
     * 1 - 正常
     */
    @JsonProperty("elementStatus")
    private Integer status;

    /**
     * 异常处理结果
     * 0 - 未处理
     * 1 - 已处理
     * -1 - 无值
     */
    private Integer abnormalResult;

    /**
     * 处理方式
     * 1 - 人共排查
     * 2 - 故障报修
     * -1 - 无值
     * 可能这一项有值而Result无值
     * 当处理方式是故障报修时需要创建一个工单
     */
    private Integer abnormalMethod;

    /**
     * 是否超时，true false
     * 此字段用来统计补检个数，如果是超时，则补检个数则需要+1
     */
    private Boolean isOvertime;

    /**
     * 图片List
     */
    private ArrayList<String> images;

    /**
     * 故障描述，可能无此项
     */
    private String faultDescription;

    /**
     * 操作人员
     */
    private String operator;

    /**
     * 0 - 综合类型
     * 1 - 设备类型
     */
    private Integer type;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 工单创建者id
     */
    private String creator;

    /**
     * 工单创建者名称
     */
    private String creatorName;

    /**
     * 工作组id
     */
    private String workTeamId;

    /**
     * 工作组名称
     */
    private String workTeamName;

    /**
     * 预计开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp preStartTime;

    /**
     * 预计结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp preEndTime;

    /**
     * 工单名称
     */
    private String title;

    /**
     * 审批人Id
     */
    private String auditId;

    /**
     * 审批人name
     */
    private String auditName;

}
