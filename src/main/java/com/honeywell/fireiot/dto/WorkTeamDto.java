package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;


@Data
public class WorkTeamDto {


    private String id;

    private String teamName;
    private List<String> auditIds;
    private List<String> auditNames;
    private List<String> workerIds;
    private List<String> workerName;
    private List<String> tracerIds;
    private List<String> tracerName;

    private List<String> verifyIds;
    private List<String> verifyNames;

    private List<String> saveIds;
    private List<String> saveNames;
    private List<String> allIds;
    private String description;



}
