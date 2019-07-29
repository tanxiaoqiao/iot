package com.honeywell.fireiot.dto;


import com.honeywell.fireiot.constant.NotifyWayEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class WarehouseDto implements Serializable {

    private  Long id;
    private String name;
    private ArrayList<Long> adminIdList;
    private Long locationId;
    private String description;
    private NotifyWayEnum notifyWay;
}
