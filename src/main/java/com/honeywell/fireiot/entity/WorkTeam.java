package com.honeywell.fireiot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


@Data
@Table(name = "us_workteam")
@Entity
@Where(clause = " deleted = 0 ")
@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
@ToString(exclude = "audits")
public class WorkTeam {


    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid2")
    private String id;


    private String teamName;
    //主管
    @Column(name = "audit_ids")
    private String[] auditIds;
    //排程派工
    @Column(name = "worker_ids")
    private String[] workerIds;
    //追踪
    @Column(name = "tracer_ids")
    private String[] tracerIds;
    //验证
    @Column(name = "verify_ids")
    private String[] verifyIds;
    //存档
    @Column(name = "save_ids")
    private String[] saveIds;
    @Column(name = "all_ids")
    private String[] allIds;
    private String description;

    @CreatedDate
    private Timestamp createTime;

    @LastModifiedDate
    private Timestamp updateTime;

    /**
     * 0 - 未删除
     * 1 - 删除
     */
    @JsonIgnore
    private Integer deleted = 0;


}
