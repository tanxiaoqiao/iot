package com.honeywell.fireiot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "location Tree对象", description = "location Tree描述")
public class LocationTree {
    @ApiModelProperty(value = "name", name = "name")
    private String name;
    @ApiModelProperty(value = "level", name = "level")
    private Integer level;
    @ApiModelProperty(value = "sort", name = "sort")
    private long sort;
    @ApiModelProperty(value = "children", name = "children")
    private List<LocationTree> children;
    private String fullName;
}
