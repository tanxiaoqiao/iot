package com.honeywell.fireiot.dto;

import com.honeywell.fireiot.entity.Maintenance;
import com.honeywell.fireiot.entity.Step;
import com.honeywell.fireiot.entity.WorkorderDevice;
import com.honeywell.fireiot.entity.WorkorderLocation;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kris
 * @Date: 2019-07-05  14:53
 */
@Data
public class MaintenanceDto {

    private Long id;
    private String name;

    private String workteamId;
    private String workteamName;
    private ArrayList<Step> steps;
    private String description;
    private Integer level ;

    private List<WorkorderDevice> deviceIds;

    private List<WorkorderLocation> locationIds;
    private Integer times;

    private Integer type;

    private Timestamp startTime;

    private Boolean isAuto;

    private Integer days;

    private Boolean saveAuto;

    private Integer workDays;

    private Integer startMonth;

    private Integer endMonth;

    public static MaintenanceDto toDto(Maintenance maintenance){
        MaintenanceDto md = new MaintenanceDto();
        BeanUtils.copyProperties(maintenance,md);
        return md;
    }
}
