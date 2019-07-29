package com.honeywell.fireiot.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kris
 * @Description: 用户定义表
 * @Date: 8/6/2018 2:34 PM
 * @Update: zhenzhong.wang
 */
@Entity
@Table(name = "us_user")
@Data
@ToString(exclude = {"roles"})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "username can not be null")
    private String username;

//    @NotBlank(message = "eid can not be null")
//    @Column(unique = true)
    private String eid;

    private String userId;

    private String email;

    private String password;
    //1启动， 2删除
    private Integer status = 1;

    private String name;

    private String phone;

    private String creator;

    @Column(name = "create_time")
    @CreatedDate
    private Timestamp createTime;

    @Column(name = "update_time")
    @LastModifiedDate
    private Timestamp updateTime;
    /*
        //用户组
        @Column(name = "user_group")
        private String userGroup;*/
    //zh_CN,en_US
    private String language;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "us_user_role_rel", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "id")
    })
    private List<Role> roles;

    @Transient
    private List<Integer> mobileAccess = new ArrayList<>();

    private Integer resource;
    private String dsc;  // 描述

    public User() {

    }

    public User(Long id, String username, String email,
                String password, Integer status, String name, String phone, String creator,
                String language, List<Role> roles, String eid, String userId, String dsc) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.name = name;
        this.phone = phone;
        this.creator = creator;
        this.language = language;
        this.roles = roles;
        this.eid = eid;
        this.userId = userId;
        this.dsc = dsc;
    }
}
