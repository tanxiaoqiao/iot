package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AppFaultDTO
 * @Description TODO
 * @Author Monica Z
 * @Date 2019-07-24 08:47
 */
@Data
public class AppFaultDTO {
    /**
     * 工单名称
     */
    private String title;
    /**
     * 工单处理工作组id
     */
    private String workTeamId;
    /**
     * 工单处理工作组名称
     */

    private String workTeamName;

    /**
     * 工单审核人
     */
    private String auditId;
    private String auditName;

    /**
     * 相关设备
     */
    private ArrayList<Long> deviceIds;

    /**
     * 空间位置
     */
    private ArrayList<Long> locationIds;
    /**
     * 故障描述
     */
    private String description;

    private String creator;

    private String creatorName;





}
