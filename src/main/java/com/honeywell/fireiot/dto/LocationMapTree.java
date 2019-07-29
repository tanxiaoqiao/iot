package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

@Data
public class LocationMapTree {
    private String name;
    private Integer level;
    private long id;
    private long parentId;
    private List<LocationMapTree> children;

    // 底图资源文件
    private String mapFile;
}
