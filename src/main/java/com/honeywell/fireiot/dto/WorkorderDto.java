package com.honeywell.fireiot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honeywell.fireiot.entity.*;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:3/19/2019
 */
@Data
public class WorkorderDto implements Serializable {

    private Long id;

    private String creator;

    private String creatorName;

    private String title;

    private String processId;

    //0已创建 1待派单 2待处理 3已完成 4已终止 5已验证
    private Integer status = 0;

    //优先级 0一般 1紧急 2十分紧急
    private Integer level;

    private Integer type;

    //工作组
    private String workTeamId;
    private String workTeamName;


    //实际接收工作人
    private ArrayList<String> acceptorIds;
    private ArrayList<String> acceptorName;
    //实际接收工作人
    private String actAcceptor;
    //实际接收工作人
    private String actAcceptorName;

    private String description;


    //预计开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp preStartTime;
    //预计结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp preEndTime;
    //实际开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp actStartTime;
    //实际结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp actEndTime;

    private String attachUrl;
    //操作记录

    private List<HistoryOperation> historyOperations;

    //设备信息
    private List<WorkorderDevice> workorderDevice;

    //空间位置
    private List<WorkorderLocation> workorderLocation;

    //计划性维护
    private Long dailyMaintenanceId;


    private ArrayList<Step> steps;

    private String mobile;

    public static WorkorderDto toDto(Workorder wo){
        WorkorderDto wd = new WorkorderDto();
        BeanUtils.copyProperties(wo,wd);
        return wd;
    }


}
