package com.honeywell.fireiot.security.login;

import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.security.entity.AuthorityUser;
import com.honeywell.fireiot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


/**
 * 用户登录服务，为spring-security提供用户信息
 *
 * @Author: zhenzhong.wang
 * @Time: 2018/2/7 19:11
 */
@Component("userDetailsService")
public class DefaultUserDetailsServiceImpl implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger("security");

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthorityUser authorityUser = new AuthorityUser();
        User user = userService.findUserByUsernameOrEmail(username);

        if (user != null) {
            BeanUtils.copyProperties(user, authorityUser);
            // 置空Role中的User，避免死循环问题
            if (authorityUser.getRoles() != null) {
                for (int i = 0; i < authorityUser.getRoles().size(); i++) {
                    authorityUser.getRoles().get(i).setUsers(null);
                }
            }
            // 更新用户状态
            authorityUser.setSecurityStatus(true, true, true);
//            userDao.updateLastLoginTime(user.getId());
        }
        return authorityUser;
    }

}
