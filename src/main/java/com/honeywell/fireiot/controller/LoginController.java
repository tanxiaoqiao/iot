package com.honeywell.fireiot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.TraceLogType;
import com.honeywell.fireiot.entity.TraceLog;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.repository.TraceLogRepository;
import com.honeywell.fireiot.security.SessionManager;
import com.honeywell.fireiot.service.UserService;
import com.honeywell.fireiot.sso.SSORestApi;
import com.honeywell.fireiot.utils.EnvHolder;
import com.honeywell.fireiot.utils.ResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录控制
 * 当SSO启用时生效，SSO关闭时，登录登出接口将被spring security接替
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/1/2 1:17 PM
 */
@Api(tags = "登录模块")
@RestController
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;
    @Autowired
    TraceLogRepository logRepository;

    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public ResponseObject login(
            HttpServletResponse response,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "isMobile", required = false, defaultValue = "false") boolean isMobile) {
        JSONObject result = null;
        String userID = null;
        List<Integer> mobileAccess = new ArrayList<>();

        // authorization不为空时，调用sso判断用户是否已登录
        if (StringUtils.isNotEmpty(authorization)) {
            ResponseObject resObj = SSORestApi.verifyToken(authorization);

            if (resObj.getCode() == 8007) {
                // 校验通过
                result = JSON.parseObject(resObj.getData().toString());
                username = result.get("userName").toString();
                userID = result.get("userID").toString();
            }

        } else {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                throw new BusinessException(ErrorEnum.USERNAME_PASSWORD_NOT_NULL);
            }

            // 调取sso登录接口
            ResponseObject resObj = SSORestApi.ssoLogin(username, password, isMobile);
            // 登录失败
            if (resObj.getCode() != 8005) {
                logger.warn("{} 登录失败：{}", username, resObj.getMsg());
                return ResponseObject.fail(ErrorEnum.PASSWORD_ERROR);
            }
            result = JSON.parseObject(resObj.getData().toString());
            userID = result.get("userID").toString();
            mobileAccess = (List<Integer>) result.get("mobileAccess");
        }


        User loginUser = userService.findUserByUsernameOrEmail(username);
        if (loginUser == null) {
            // 新增用户
//            loginUser = userService.addBusinessUser(username, password, userID);
//            logger.info("本地新增用户：{}，userID：{}", username, userID);
            return ResponseObject.fail(ErrorEnum.NO_USER);
        }

        // 注册用户
        loginUser.setPassword(null);
        loginUser.setMobileAccess(mobileAccess);
        SessionManager.registerUser(loginUser);

        // 设置sso_token
        Cookie sso_token = new Cookie(SSORestApi.ssoTokenName, authorization == null ? result.get("token").toString() : authorization);
        response.addCookie(sso_token);
        // 为移动端在header中设置sso_token
        response.setHeader(SSORestApi.ssoTokenName, authorization == null ? result.get("token").toString() : authorization);

        logger.info("用户 {} 已登录", username);

        TraceLog traceLog = new TraceLog(
                TraceLogType.LOGIN,
                "用户 " + username + " 已登录",
                username,
                LocalDateTime.now());
        traceLog.setResource(EnvHolder.getHolder().getResource());
        logRepository.save(traceLog);

        return ResponseObject.success(loginUser);
    }
}
