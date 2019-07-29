package com.honeywell.fireiot.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: create by kris
 * @description:
 * @date:1/21/2019
 */
@Data
public class TeamDto implements Serializable {


    private String id;

    private String teamName;
    private List<PositionDto> audits;
    private List<PositionDto> workers;
    private List<PositionDto> tracers;
    private List<PositionDto> verifys;
    private List<PositionDto> saves;
    private List<PositionDto> alls;
    private String description;


}
