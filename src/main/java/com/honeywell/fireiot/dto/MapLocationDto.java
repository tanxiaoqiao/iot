package com.honeywell.fireiot.dto;

import lombok.Data;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 2:06 PM 4/17/2019
 */
@Data
public class MapLocationDto {

    private String name;
    private String id;
    private String systemType;
    private String type;
    private String mapLocation;

    private String loop;
    private String zone;
    private String point;
    private String network;

}
