package com.honeywell.fireiot.interceptor;

import com.honeywell.fireiot.entity.Env;
import com.honeywell.fireiot.utils.EnvHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 环境信息拦截
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/4/11 11:51 AM
 */
public class EnvInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Env env = EnvHolder.getHolder();

        if (request.getHeader("resource") != null) {
            env.setResource(Integer.parseInt(request.getHeader("resource")));
        }

        EnvHolder.setHolder(env);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        EnvHolder.remove();
    }
}
