package com.honeywell.fireiot.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Author: Kris
 * @Description: 角色对象
 * @Date: 8/6/2018 2:34 PM
 */
@Entity
@Table(name = "us_role", uniqueConstraints = @UniqueConstraint(columnNames = {"systemType", "name"}))
@Data
@ToString(exclude = {"roleResourceRels", "users"})
public class Role extends BaseEntity<Role> {

    @NotBlank(message = "name_cant_be_null")
    @Column(unique = true)
    private String name;
    private String description;
    private String menuId;       // 菜单列表，用逗号分隔
    private String systemType;   // 系统类型 systemType+name可定义唯一角色

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @JsonIgnore
    @JSONField(serialize = false)
    private List<RoleResourceRel> roleResourceRels;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    @JsonIgnore
    @JSONField(serialize = false)
    private List<User> users;
}
