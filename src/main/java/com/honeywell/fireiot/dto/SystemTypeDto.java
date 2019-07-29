package com.honeywell.fireiot.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
public class SystemTypeDto {

    private Long id;

    @NotEmpty(message = "系统类型名称不能为空")
    private String name;
    private String description;
    @NotEmpty(message = "系统编号不能为空")
    private String systemNo;
    private Long parentId;
    private String code;
    private String fullName;
    private List<SystemTypeDto> children = new ArrayList<>();
}
