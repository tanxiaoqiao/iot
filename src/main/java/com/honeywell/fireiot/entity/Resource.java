package com.honeywell.fireiot.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/**
 * @Author: Kris
 * @Author: zhenzhong.wang
 * @Description: 权限对象
 * @Date: 8/6/2018 2:34 PM
 */
@Entity
@Table(name = "us_resource")
@Data
@ToString(exclude = "roleResourceRels")
public class Resource extends BaseEntity<Resource> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    //get post patch
    private String method;
    private String description;

    @OneToMany(mappedBy = "resource")
    @JsonIgnore
    @JSONField(serialize = false)
    private List<RoleResourceRel> roleResourceRels;
}
