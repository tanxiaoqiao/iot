package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Entity
@Table(name = "wo_workorder")
@Data
@ToString(exclude = "historyOperations")
public class Workorder extends BaseEntity<Workorder> {

    @Column
    private String creator;

    @Column
    private String creatorName;

    @Column(name = "process_id")
    private String processId;

    private String title;


    //0已创建 1待派单 2待处理 3已完成 4已终止 5已验证 6未审核 7已审核 -1维保待审核
    private Integer status = 0;


    //优先级 0一般 1紧急 2十分紧急
    private Integer level;
    //0自定义 1维保 2报故障
    private Integer type;

    //工作组
    @Column(name = "work_team_id")
    private String workTeamId;
    @Column(name = "work_team_name")
    private String workTeamName;

    private Integer auditStatus;


    @Column(name = "audit_id")
    private String auditId;
    @Column(name = "audit_name")
    private String auditName;

    //派单
    @Column(name = "acceptor_ids")
    private ArrayList<String> acceptorIds;
    @Column(name = "acceptor_name")
    private ArrayList<String> acceptorName;
    //实际接收工作人
    @Column(name = "act_acceptor")
    private String actAcceptor;
    //实际接收工作人
    @Column(name = "act_acceptor_name")
    private String actAcceptorName;

    private String description;

    //预计开始时间
    @Column(name = "pre_start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp preStartTime;
    //预计结束时间
    @Column(name = "pre_end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp preEndTime;
    //实际开始时间
    @Column(name = "act_start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp actStartTime;
    //实际结束时间
    @Column(name = "act_end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp actEndTime;

    @Column(name = "attach_url")
    private String attachUrl;
    //操作记录
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "workorder")
    @JsonIgnore
    private List<HistoryOperation> historyOperations;

    //设备信息
//    @Column(name = "workorder_device")
//    private ArrayList<WorkorderDevice> workorderDevice;
    private ArrayList<Long> deviceIds;

    //    //空间位置
//    @Column(name = "workorder_location")
//    private ArrayList<WorkorderLocation> workorderLocation;
    private ArrayList<Long> locationIds;


    //计划性维护
    @Column(name = "daily_maintenance_id")
    private Long dailyMaintenanceId;

    //计划性维护
    @Column
    private ArrayList<Step> steps;

    @Column
    private Boolean saveAuto = false;

    private String mobile;

    private Long patrolId;
    private String endDescription;

    @OneToMany(mappedBy = "workorders",cascade = CascadeType.ALL,orphanRemoval=true)
    @JsonIgnore
    private List<WorkorderDeviceRel> workorderDeviceRels=new ArrayList<>();

    @OneToMany(mappedBy = "workorders",cascade = CascadeType.ALL,orphanRemoval=true)
    @JsonIgnore
    private List<WorkorderLocationRel> workorderLocationRels=new ArrayList<>();;
}
