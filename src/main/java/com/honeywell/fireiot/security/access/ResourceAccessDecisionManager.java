package com.honeywell.fireiot.security.access;

import com.honeywell.fireiot.security.WebSecurityConfig;
import com.honeywell.fireiot.utils.SecurityUtil;
import com.honeywell.fireiot.utils.SessionUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Iterator;

/**
 * 访问决策管理器，只在用户登录后生效
 *
 * @Author: zhenzhong.wang
 * @Time: 2018/2/9 15:17
 */
public class ResourceAccessDecisionManager implements AccessDecisionManager {


    /**
     * 判断是否为token请求
     * @return
     */
    public boolean isToken() {
        if (SessionUtils.getRequest() != null) {
            if (WebSecurityConfig.authToken.equals(SessionUtils.getRequest().getHeader("token"))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        /**
         * 判断用户是否为匿名用户
         * 此处出现匿名用户的情况有两种：一种是用户未登录时发送请求，一种是用户已登录但请求允许匿名访问的接口。
         * 匿名用户的请求直接返回，当前决策器不做校验
         */
        if (SecurityUtil.isAnonymousUrl(SessionUtils.getRequest())) {
            return;
        }

        // token请求不做403校验
        if (isToken()) {
            return;
        }

        if (configAttributes == null) {
            throw new AccessDeniedException("");
        }

        Iterator<ConfigAttribute> ite = configAttributes.iterator();


        /*
         * 访问控制，登陆用户有一个相应权限就可以访问
         */
        if (authentication != null) {
            Collection<? extends GrantedAuthority> gaList = authentication.getAuthorities();
            if (gaList != null && !gaList.isEmpty()) {
                while (ite.hasNext()) {

                    ConfigAttribute ca = ite.next();
                    String needRole = ((SecurityConfig) ca).getAttribute();

                    /*
                     * ga 为用户所被赋予的权限; needRole为访问相应的资源（URL-Method）应该具有的权限。
                     */
                    for (GrantedAuthority ga : gaList) {
                        if (needRole.equalsIgnoreCase(ga.getAuthority())) {
                            return;
                        }
                    }
                }
            }
        }
        throw new AccessDeniedException("access denied !");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
