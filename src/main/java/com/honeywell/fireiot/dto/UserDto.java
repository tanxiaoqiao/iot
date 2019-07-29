package com.honeywell.fireiot.dto;

import com.honeywell.fireiot.entity.Role;
import com.honeywell.fireiot.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kris
 * @Description:
 * @Date: 8/7/2018 9:17 AM
 */
@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String eid;
    private String email;
    private String password;
    //0在用 1禁用 2删除
    private Integer status;
    private String name;
    private String phone;
    private String creator;

    //zh_CN,en_US
    private String language;

    private List<Role> roles;
    private Long[] roleIds;
    private List<Integer> mobileAccess = new ArrayList<>();
    private String userId;
    private Integer resource;
    private String dsc;

    public UserDto(){}

    public UserDto(User user) {
        BeanUtils.copyProperties(user, this);
    }

}
