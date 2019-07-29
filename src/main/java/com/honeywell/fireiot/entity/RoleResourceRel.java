package com.honeywell.fireiot.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @Author: zhenzhong.wang
 * @Description: 角色权限关系表
 */
@Entity
@Table(name = "us_role_resource_rel")
@Data
public class RoleResourceRel extends BaseEntity<RoleResourceRel> {

    @ManyToOne
    private Role role;
    @ManyToOne
    private Resource resource;

    public RoleResourceRel() {}

    public RoleResourceRel(Role role, Resource resource) {
        this.role = role;
        this.resource = resource;
    }
}
