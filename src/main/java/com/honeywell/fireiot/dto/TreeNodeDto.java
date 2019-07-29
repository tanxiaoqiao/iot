package com.honeywell.fireiot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @project: fire-user
 * @name: TreeNodeDto
 * @author: dexter
 * @create: 2018-12-11 16:32
 * @description:
 **/
@Data
public class TreeNodeDto<T> {

    @JsonProperty("id")
    private T id;

    private T parentId;

    @JsonProperty("value")
    private String name;

    @JsonProperty("children")
    private List<TreeNodeDto<T>> children;

    private boolean isLeaf = true;

    private Integer level;
}
