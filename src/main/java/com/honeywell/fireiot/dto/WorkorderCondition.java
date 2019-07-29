package com.honeywell.fireiot.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Data
public class WorkorderCondition {

    private Long Id;
    private String employeeId;
    private String creator;
    private List<Integer> status;
    private String title;
    private Integer type;
    private Integer level;
    private Long deviceId;
    private Long locationId;
    private Integer pi=1;
    private Integer ps=15;

    @JsonIgnore
    private String locationIds;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTimeStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createTimeEnd;
}
