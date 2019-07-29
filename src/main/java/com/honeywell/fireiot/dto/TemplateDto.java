package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TemplateDto {
    private long id;
    private String name;
    /**
     * 模版类型
     * 0 其他
     * 1 设备
     */
    private Integer type;
    private String  comments;
    private Date createTime;
    private Date updateTime;


}
