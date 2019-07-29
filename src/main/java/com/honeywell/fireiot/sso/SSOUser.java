package com.honeywell.fireiot.sso;

import com.honeywell.fireiot.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户创建dto，用于SSO创建用户
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/1/2 8:12 PM
 */
@Data
public class SSOUser {

    private String id;
    private String userName;
    private String eid;
    private String fullName;
    private String password;

    private int resource;
    private String email;
    private String cellPhone;
    private String description;
    private List<Integer> applications = new ArrayList<>();
    private List<Integer> mobileAccess = new ArrayList<>();

    public SSOUser() {}

    public SSOUser(User user) {
        this.userName = user.getUsername();
        this.fullName = user.getName();
        this.cellPhone = user.getPhone();
        this.email = user.getEmail();
        this.eid = user.getEid();
        this.id = user.getUserId();
        this.mobileAccess = user.getMobileAccess();
        this.description = user.getDsc();
    }

    /**
     * 映射ssoUser中的属性到系统User中
     * @param user
     * @return
     */
    public void reflectSSOField(User user) {
        BeanUtils.copyProperties(this, user, "password", "resource");
        user.setUsername(this.userName);
        user.setEid(this.eid);
//        user.setPhone(this.cellPhone);
        user.setUserId(this.id);
        user.setMobileAccess(this.mobileAccess);
        user.setDsc(this.description);
    }
}
