package com.honeywell.fireiot.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeywell.fireiot.entity.Step;
import com.honeywell.fireiot.entity.WorkorderDevice;
import com.honeywell.fireiot.entity.WorkorderLocation;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Data
public class SaveWorkorderVo {

    private String creator;


    private String creatorName;

    private String processId;

    private String title;


    //0已创建 1已派单 2待处理 3已完成 4已终止 5已验证 6待审批
    private Integer status = 0;

    //优先级 0一般 1紧急 2十分紧急
    private Integer level = 0;

    //工作组
    private String workTeamId;
    private String workTeamName;


    //实际接收工作人
    private ArrayList<String> acceptorIds;
    private ArrayList<String> acceptorName;


    private String auditId;
    private String auditName;

    //实际接收工作人
    private String actAcceptor;
    //实际接收工作人
    private String actAcceptorName;

    private String description;

    //预计开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Timestamp preStartTime;
    //预计结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Timestamp preEndTime;


    private String attachUrl;
    private ArrayList<Long> deviceIds;
    private ArrayList<Long> locationIds;


    //计划性维护
    private Long dailyMaintenanceId;

    //计划性维护
    private ArrayList<Step> steps;

    private Integer type;
    private Boolean saveAuto = false;

    private String mobile;

    private Long patrolId;
}
