package com.honeywell.fireiot.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 角色Dto
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/3/27 10:29 AM
 */
@Data
public class RoleDto {
    private Long id;

    @NotEmpty(message = "name can not be null")
    private String name;
    private String description;
    private String[] menuIds;       // 菜单列表，用逗号分隔
    private Long[] resourceIds;
    @NotEmpty(message = "system type can not be null")
    private String systemType;      // 系统类型，表示当前角色为哪个系统的角色

}
