package com.honeywell.fireiot.entity;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统类型
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/19 4:01 PM
 */
@Data
@Entity
@Table(name = "dev_system_type")
@ToString(exclude = {"parentSystemType"})
@DynamicUpdate
public class SystemType extends BaseEntity<SystemType>{
    // 消防系统 水系统（中文描述）
    private String name;//系统类型名字
    //系统类型编码
    @Column(name = "system_code")
    private String code;

    //polling有
    //上级系统
    @ManyToOne(cascade = { CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @OrderBy(value = "name ASC")
    private SystemType parentSystemType;

    // 子系统列表
    @OneToMany(mappedBy = "parentSystemType", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @OrderBy(value = "name ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<SystemType> childList = new ArrayList<>();

    @Column
    private String fullName;//全称

    @OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="systemType" ,orphanRemoval = true)
    private List<DeviceType> deviceTypes;//设备类型

}
