package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;


@Data
@Table(name = "user_team")
@Entity
public class UserTeam implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "team_name")
    private String teamName;

    private String audit;

    @Column(name = "audit_name")
    private String auditName;

    private ArrayList<String> workers;

    @Column(name = "worker_name")
    private ArrayList<String> workerName;
    @Column(name = "tracers")
    private ArrayList<String> tracers;

    @Column(name = "tracer_name")
    private ArrayList<String> tracername;

    //0为删除 1 已删除
    private Integer status = 0;


}
