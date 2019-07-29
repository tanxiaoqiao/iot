package com.honeywell.fireiot.dto;

import com.honeywell.fireiot.entity.EmployeeRelations;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;

/**
 * @project: fire-user
 * @name: EmployeeRelationsDto
 * @author: dexter
 * @create: 2018-12-20 10:38
 * @description:
 **/
@Data
@NoArgsConstructor
public class EmployeeRelationsDto {

    protected String id;

    @NotBlank(message = "employee_id_not_null")
    private String employeeId;

    private String occupationId;

    private String departmentId;

    /**
     * 空间位置id
     * 需要存储最后一级id
     */
    private String positionId;

    /**
     * id以|分隔
     */
    private String workTeamIds;

    private String userId;

    private String userName;

    public EmployeeRelationsDto(EmployeeRelations employeeRelations) {
        BeanUtils.copyProperties(employeeRelations, this);
    }
}
