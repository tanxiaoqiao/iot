package com.honeywell.fireiot.dto;

import com.honeywell.fireiot.entity.Department;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @project: fire-user
 * @name: DepartmentDto
 * @author: dexter
 * @create: 2018-12-17 15:56
 * @description:
 **/
@Data
@NoArgsConstructor
public class DepartmentDto {

    protected String id;

    private String name;

    private String code;

    private String description;

    private Integer level;

    private String parentId;

    private String fullName;

    public DepartmentDto(Department department) {
        BeanUtils.copyProperties(department, this);
    }
}
