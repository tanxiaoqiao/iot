package com.honeywell.fireiot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.honeywell.fireiot.entity.Employee;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;

/**
 * @project: fire-user
 * @name: EmployeeDto
 * @author: dexter
 * @create: 2018-12-19 14:41
 * @description:
 **/
@Data
@NoArgsConstructor
public class EmployeeDto {

    protected String id;

    @NotBlank(message = "employee_name_not_null")
    private String name;

    private String employeeNo;

    private String telephone;

    private String mobile;

    /**
     * 类型
     * 0 - 员工
     */
    private Integer type;

    private String email;

    private String description;

    /**
     * 0 - 在职
     * 1 - 离职
     */
    @NotBlank(message = "employee_status_not_null")
    private Integer status;

    private String technicals;

    /**
     * 头像存储路径
     */
    private String picture;

    @JsonProperty("relations")
    private EmployeeRelationsDto employeeRelationsDto;

    public EmployeeDto(Employee employee) {
        BeanUtils.copyProperties(employee, this);
    }
}
