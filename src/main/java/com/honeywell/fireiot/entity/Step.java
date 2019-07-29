package com.honeywell.fireiot.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class Step implements Serializable {
    private String name;
    private String description;
    private Boolean isDone;

}
