package com.honeywell.fireiot.utils;

import com.alibaba.fastjson.JSONObject;
import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.security.entity.AuthorityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * session工具类
 *
 * @Author: zhenzhong.wang
 * @Time: 2018/09/29 18:35
 */
public class SessionUtils {


    private static Logger logger = LoggerFactory.getLogger(SessionUtils.class);

    /**
     * 获取当前用户
     *
     * @return
     */
    public static UserDto getCurrentUser() {
        Object currentUser = getRequest().getSession().getAttribute("CURRENT_USER");
        if (currentUser != null) {
            return JSONObject.parseObject(currentUser.toString(), UserDto.class);
        }

        // 当session中获取当前用户失败时，尝试从SecurityContextHolder中获取
        AuthorityUser principle = getPrinciple();
        if (principle != null) {
            UserDto userDto = new UserDto(principle.toUser());
            if (userDto != null) {
                return userDto;
            }
        }
        return null;
    }

    public static AuthorityUser getPrinciple() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && UserDetails.class.isAssignableFrom(principal.getClass())) {
            AuthorityUser user = (AuthorityUser) principal;
            return user;
        } else {
            return null;
        }
    }


    /**
     * 获取当期线程Request
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        return request;
    }

    /**
     * 获取当前线程Response
     *
     * @return
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletResponse response = requestAttributes.getResponse();
        return response;
    }

    /**
     * 注册token用户
     */
    public static void registerTokenUser() {

    }


}
