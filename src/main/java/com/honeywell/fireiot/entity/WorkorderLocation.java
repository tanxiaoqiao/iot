package com.honeywell.fireiot.entity;

import lombok.Data;


@Data
public class WorkorderLocation{
    private Long id;
    private String name;


    public static WorkorderLocation toDto(Location wo) {
        WorkorderLocation wd = new WorkorderLocation();
        wd.setId(wo.getId());
        wd.setName(wo.getName());
        return wd;
    }
}
