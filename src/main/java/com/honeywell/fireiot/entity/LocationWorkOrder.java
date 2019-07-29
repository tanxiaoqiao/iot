package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
/**
 * @Author: Kris
 * @Date: 2019-07-08  09:33
 */
@Entity
@Table(name = "location_work_order")
@Data
public class LocationWorkOrder implements Serializable {

    @Id
    private Long id;

    private String creator;

    private String creatorName;

    private String title;


    //0已创建 1待派单 2待处理 3已完成 4已终止 5已验证 6未审核 7已审核
    private Integer status ;


    //优先级 0一般 1紧急 2十分紧急
    private Integer level;
    //0自定义 1维保 2报故障
    private Integer type;

    //工作组
    private String workTeamId;
    private String workTeamName;


    private String auditId;
    private String auditName;
//
//    //实际接收工作人
//    private String actAcceptor;
//    //实际接收工作人
//    private String actAcceptorName;

    private String description;


    private String employeeId;
    //预计开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp preStartTime;
    //预计结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp preEndTime;
    //实际开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp actStartTime;
    //实际结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Timestamp actEndTime;

    private Long locationId;



    private Long deviceId;


}
