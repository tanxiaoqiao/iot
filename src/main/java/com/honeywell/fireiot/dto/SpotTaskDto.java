package com.honeywell.fireiot.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName SpotTaskDto
 * @Description 点位工作任务关联表，
 * @Author Monica Z
 * @Date 2019/1/14 10:54
 */
@Data
public class SpotTaskDto {
    private long id;
    private String name;
    private String fullName;
    private long locationId;
    private List<TaskDto> taskDtoList;
}
