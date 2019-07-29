package com.honeywell.fireiot.security.entity;

import com.honeywell.fireiot.entity.Role;
import com.honeywell.fireiot.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 授权用户，用于登录控制
 *
 * @Author: zhenzhong.wang
 * @Time: 2018/9/11 18:28
 */
@Data
public class AuthorityUser extends User implements UserDetails {

    private boolean accountNonExpired;      // 账户未过期
    private boolean accountNonLocked;       // 账户未被锁
    private boolean credentialsNonExpired;  // 认证未过期

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<AuthorityRole> authorityRoles = new ArrayList<>();
        List<Role> roles = super.getRoles();
        if (roles != null) {
            roles.forEach(role -> {
                authorityRoles.add(new AuthorityRole(role.getSystemType() + "-" + role.getName()));
            });
        }
        return authorityRoles;
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 设定账户安全状态
     *
     * @param accountNonLocked
     * @param accountNonExpired
     * @param credentialsNonExpired
     */
    public void setSecurityStatus(Boolean accountNonLocked, Boolean accountNonExpired, Boolean credentialsNonExpired) {
        if (accountNonLocked != null) {
            this.accountNonLocked = accountNonLocked;
        }
        if (accountNonExpired != null) {
            this.accountNonExpired = accountNonExpired;
        }
        if (credentialsNonExpired != null) {
            this.credentialsNonExpired = credentialsNonExpired;
        }
    }

    /**
     * 转换成User
     *
     * @return
     */
    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
