package com.honeywell.fireiot.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.honeywell.fireiot.config.ApplicationContextProvider;
import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.security.entity.AuthorityUser;
import com.honeywell.fireiot.security.login.DefaultUserDetailsServiceImpl;
import com.honeywell.fireiot.service.UserService;
import com.honeywell.fireiot.sso.SSORestApi;
import com.honeywell.fireiot.utils.EnvHolder;
import com.honeywell.fireiot.utils.ResponseObject;
import com.honeywell.fireiot.utils.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Session管理
 *
 * @Author: zhenzhong.wang
 * @Time: 2018/3/12 11:27
 */
@Service
public class SessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    private static SessionRegistry sessionRegistry;
    private static DefaultUserDetailsServiceImpl userDetailsService;

    public static SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    @Autowired
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        SessionManager.sessionRegistry = sessionRegistry;
    }

    @Autowired
    public void setUserDetailsService(DefaultUserDetailsServiceImpl userDetailsService) {
        SessionManager.userDetailsService = userDetailsService;
    }

    /**
     * 注册用户到内存，模拟登陆
     *
     * @param user
     */
    public static void registerUser(User user) {
        String username = user.getUsername();
        if (username != null) {
            // 检查用户名是否和当前登录用户相同，如果相同则跳过避免重复登录
            UserDto currentUser = SessionUtils.getCurrentUser();
            if (currentUser != null &&
                    (username.equals(currentUser.getUsername()) || username.equals(currentUser.getEmail()))) {
                logger.debug("User:" + username + " has been in system, skip login...");
                return;
            }

            // 向Spring Security中注册用户
            AuthorityUser authorityUser = new AuthorityUser();
            BeanUtils.copyProperties(user, authorityUser);
            // 置空Role中的User，避免死循环问题
            if (authorityUser.getRoles() != null) {
                for (int i = 0; i < authorityUser.getRoles().size(); i++) {
                    authorityUser.getRoles().get(i).setUsers(null);
                    authorityUser.getRoles().get(i).setRoleResourceRels(null);
                }
            }
            // 更新用户状态
            authorityUser.setSecurityStatus(true, true, true);
            Authentication auth = new UsernamePasswordAuthenticationToken(authorityUser, null, authorityUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            // 注册session
            sessionRegistry.registerNewSession(SessionUtils.getRequest().getSession().getId(), authorityUser);
        }
    }

    public static void ssoLogin() {
        String sso_token = EnvHolder.getHolder().getSsoToken();

        // sso_token为null，判定为未登录
        if (StringUtils.isEmpty(sso_token)) {
            return;
        }

        ResponseObject resObj = SSORestApi.verifyToken(sso_token);

        // token无效
        if (resObj.getCode() != 8007) {
            return;
        }

        /** -------- 校验通过 ---------- */
        JSONObject result = JSON.parseObject(resObj.getData().toString());
        String userName = result.get("userName").toString();

        // 模拟登录
        UserService userService = (UserService) ApplicationContextProvider.getApplicationContext().getBean(UserService.class);
        User user = userService.findUserByUsernameOrEmail(userName);

        SessionManager.registerUser(user);
        return;
    }

    /**
     * 注销当前用户
     */
    public static void expireCurrentUser() {
        AuthorityUser principle = SessionUtils.getPrinciple();
        List<SessionInformation> allSessions = sessionRegistry.getAllSessions(principle, false);
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        if (allSessions != null) {
            for (int i = 0; i < allSessions.size(); i++) {
                SessionInformation sessionInformation = allSessions.get(i);
                sessionInformation.getSessionId();
                sessionInformation.expireNow();
            }
        }
    }
}
