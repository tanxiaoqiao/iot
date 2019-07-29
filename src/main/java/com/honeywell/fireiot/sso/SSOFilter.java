package com.honeywell.fireiot.sso;


import com.alibaba.fastjson.JSON;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.entity.Env;
import com.honeywell.fireiot.service.UserService;
import com.honeywell.fireiot.utils.EnvHolder;
import com.honeywell.fireiot.utils.ResponseObject;
import com.honeywell.fireiot.utils.SecurityUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SSO单点登录过滤器
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/28 3:24 PM
 */
public class SSOFilter implements Filter {

    private UserService userService;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String servletPath = request.getServletPath();
        // 当前请求允许匿名访问时略过SSO
        if (SecurityUtil.isAnonymousUrl(request)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 验证是否为token请求，如果是则略过sso校验
        String requestToken = request.getHeader("token");
        if (StringUtils.isNotEmpty(requestToken)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 1. 重定向情况，尝试从url中获取ssoToken
        String sso_token = SSORestApi.getSSOTokenFromUrl(request);

        // 2. 尝试从cookie中获取
        if (StringUtils.isEmpty(sso_token)) {
            sso_token = SSORestApi.getSSOTokenFromCookie(request);
        }

        // 3. 移动端情况，从header中获取
        if (StringUtils.isEmpty(sso_token)) {
            sso_token = SSORestApi.getSSOTokenFromHeader(request);
        }

        Env env = EnvHolder.getHolder();
        env.setSsoToken(sso_token);
        EnvHolder.setHolder(env);

        boolean loginStatus = false;
        if (StringUtils.isNotEmpty(sso_token)) {
            ResponseObject resObj = SSORestApi.verifyToken(sso_token);
            // token有效，声明登录状态
            if (resObj.getCode() == 8007) {
                loginStatus = true;
            }
        }
        // 未登录，直接返回
        if (!loginStatus && !"OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(ResponseObject.fail(ErrorEnum.NO_LOGIN)));
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
