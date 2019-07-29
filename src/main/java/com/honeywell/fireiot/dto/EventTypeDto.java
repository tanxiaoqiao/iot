package com.honeywell.fireiot.dto;

import lombok.Data;

@Data
public class EventTypeDto {

    private Long id;

    private Integer key;

    private String name;

    private String description;
}
