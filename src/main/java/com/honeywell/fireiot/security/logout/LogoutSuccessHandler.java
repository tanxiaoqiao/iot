package com.honeywell.fireiot.security.logout;

import com.alibaba.fastjson.JSON;
import com.honeywell.fireiot.config.ApplicationContextProvider;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.TraceLogType;
import com.honeywell.fireiot.entity.TraceLog;
import com.honeywell.fireiot.repository.TraceLogRepository;
import com.honeywell.fireiot.security.entity.AuthorityUser;
import com.honeywell.fireiot.sso.SSORestApi;
import com.honeywell.fireiot.utils.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 登出成功处理
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/10/25 9:43 AM
 */
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    private Logger logger = LoggerFactory.getLogger("security");

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        if (authentication == null) {
            response.getWriter().write(JSON.toJSONString(ResponseObject.fail(ErrorEnum.NO_LOGIN)));
            return;
        }
        AuthorityUser currentUser = (AuthorityUser) authentication.getPrincipal();
        logger.info("User {} has logout", currentUser.getUsername());


        // 记录登出日志
        int resource = 1;
        if (request.getHeader("resource") != null) {
            resource = Integer.parseInt(request.getHeader("resource"));
        }
        TraceLog traceLog = new TraceLog(
                TraceLogType.LOGIN,
                "用户 " + currentUser.getUsername() + " 已登出",
                currentUser.getUsername(),
                LocalDateTime.now()
                );
        traceLog.setResource(resource);
        TraceLogRepository logRepository = ApplicationContextProvider.getApplicationContext().getBean(TraceLogRepository.class);
        logRepository.save(traceLog);

        String ssoToken = SSORestApi.getSSOTokenFromCookie(request);
        if (ssoToken == null) {
            ssoToken = SSORestApi.getSSOTokenFromHeader(request);
        }
        boolean logout = SSORestApi.logout(ssoToken);
        if (logout) {
            logger.info("User {} has logout in SSO", currentUser.getUsername());
        } else {
            logger.info("User {} logout fail in SSO", currentUser.getUsername());
        }

        // 删除sso_token
        Cookie sso_token = new Cookie(SSORestApi.ssoTokenName, "");
        sso_token.setMaxAge(0);
        response.addCookie(sso_token);

        response.getWriter().write(JSON.toJSONString(ResponseObject.success("OK")));
    }
}
