package com.honeywell.fireiot.security.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

/**
 * 授权角色，用户授权控制
 *
 * @Author: zhenzhong.wang
 * @Date: 9/13/2018 10:41 AM
 */
@Data
public class AuthorityRole implements GrantedAuthority {

    private String roleName;

    public AuthorityRole() {}

    public AuthorityRole(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getAuthority() {
        return roleName;
    }
}
