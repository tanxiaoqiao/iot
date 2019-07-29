package com.honeywell.fireiot.sso;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.entity.User;
import com.honeywell.fireiot.exception.BusinessException;
import com.honeywell.fireiot.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * SSO Rest请求工具
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/28 7:56 PM
 */
@Configuration
@EnableConfigurationProperties(value = {SSOProperties.class})
public class SSORestApi {

    private static Logger logger = LoggerFactory.getLogger(SSORestApi.class);

    public static SSOProperties ssoProperties;
    public static boolean ssoEnable;

    public static final String ssoTokenName = "SSO_TOKEN";

//    private static int resource = 1;

    static SessionRegistry sessionRegistry;

    @Autowired
    public void setSsoProperties(SSOProperties ssoProperties) {
        SSORestApi.ssoProperties = ssoProperties;
        SSORestApi.ssoEnable = ssoProperties.isEnable();
    }

    @Autowired
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        SSORestApi.sessionRegistry = sessionRegistry;
    }

    /**
     * 验证token是否有效
     *
     * @param token
     * @return
     */
    public static ResponseObject verifyToken(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        String response = HttpUtils.sendGet(ssoProperties.getVerifyToken(), headers, null);
        return parseToResponseObject(response);
    }

    /**
     * SSO登出
     *
     * @param token
     * @return
     */
    public static boolean logout(String token) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        String response = HttpUtils.sendGet(ssoProperties.getLogout(), headers, null);
        ResponseObject resObj = parseToResponseObject(response);
        if (resObj.getCode() == 8006) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 调用sso登录
     *
     * @param username
     * @param password
     * @param isMobile
     * @return
     */
    public static ResponseObject ssoLogin(String username, String password, boolean isMobile) {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", username);
        data.put("resource", EnvHolder.getHolder().getResource());
        data.put("password", password);
        data.put("isMobile", isMobile);
        data.put("ip", "192.168.1.1");
        String response = HttpUtils.sendPost(ssoProperties.getLogin(), data);
        return parseToResponseObject(response);
    }

    /**
     * 更新密码
     *
     * @param userID
     * @param oldPassword
     * @param newPassword
     */
    public static boolean changePassword(String token, String userID, String oldPassword, String newPassword) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        HashMap<String, Object> reqBody = new HashMap<>();
        reqBody.put("userID", userID);
        reqBody.put("oldPassword", oldPassword);
        reqBody.put("newPassword", newPassword);
        reqBody.put("resource", EnvHolder.getHolder().getResource());
        String response = HttpUtils.sendPost(ssoProperties.getChangePassword(), headers, reqBody);

        ResponseObject resObj = parseToResponseObject(response);
        if (resObj.getCode() == 8003) {
            logger.info("SSO更新用户密码成功，userID：{}", userID);
            return true;
        } else {
            logger.warn("SSO更新用户密码失败，userID：{}, msg：{}", userID, resObj.getMsg());
            return false;
        }
    }

    /**
     * SSO创建用户
     *
     * @param user
     * @return
     */
    public static String createUser(User user, String password) {

        SSOUser ssoUser = new SSOUser(user);
        ssoUser.setResource(EnvHolder.getHolder().getResource());
        ssoUser.setApplications(Collections.emptyList());
        ssoUser.setPassword(password);

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getSSOTokenFromCookie(SessionUtils.getRequest()));
        Map<String, Object> ssoUserMap = new HashMap<>();
        ssoUserMap.put("User", ssoUser);
        String response = HttpUtils.sendPost(ssoProperties.getCreateUser(), headers, ssoUserMap);
        try {
            JSONObject result = JSONObject.parseObject(response);
            if ("8002".equals(result.get("code").toString())) {
                logger.info("SSO注册用户成功，userName：{}，userID：{}", user.getUsername(), result.get("userID").toString());
                return result.get("userID").toString();
            } else {
                logger.warn("SSO注册用户失败，userName：{}, msg：{}", user.getUsername(), result.get("errorMessage"));
            }
        } catch (Exception e) {
            logger.warn("SSO注册用户失败，userName：{}", user.getUsername());
        }
        return null;
    }

    /**
     * SSO创建用户
     *
     * @param userDto
     * @return
     */
    public static void updateUser(UserDto userDto) {

        SSOUser ssoUser = getUserByEid(userDto.getEid());
        if (ssoUser == null) {
            ssoUser = new SSOUser();
        }
        List<Integer> sourceMobileAccess = ssoUser.getMobileAccess();

        BeanUtils.copyProperties(userDto, ssoUser, "resource");
        int resource = EnvHolder.getHolder().getResource();
        ssoUser.setResource(resource);
        ssoUser.setDescription(userDto.getDsc());

        List<Integer> addApplicationAccess = new ArrayList<>();
//        if (!ssoUser.getApplications().contains(resource)) {
            addApplicationAccess.add(resource);
//        }

        // 找出新增权限的App
        List<Integer> addMobileAccess = new ArrayList<>();
        if (userDto.getMobileAccess() != null) {
            for (Integer mobile : userDto.getMobileAccess()) {
                if (sourceMobileAccess != null && !sourceMobileAccess.contains(mobile)) {
                    addMobileAccess.add(mobile);
                }
            }
        }

        // 找出删除权限的App
        List<Integer> removeMobileAccess = new ArrayList<>();
        if (sourceMobileAccess != null) {
            for (Integer mobile : sourceMobileAccess) {
                if (userDto.getMobileAccess() != null && !userDto.getMobileAccess().contains(mobile)) {
                    removeMobileAccess.add(mobile);
                }
            }
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getSSOTokenFromCookie(SessionUtils.getRequest()));
        Map<String, Object> ssoUserMap = new HashMap<>();

        PropertyFilter propertyFilter = new PropertyFilter() {
            @Override
            public boolean apply(Object object, String name, Object value) {
                if (name.equalsIgnoreCase("password")) {
                    return false;
                }
                return true;
            }
        };

//        SSOUser ssoInfo = getUserByEid(userDto.getEid());
//        ssoUser.setPassword(ssoInfo.getPassword());

        ssoUserMap.put("User", ssoUser);
        ssoUserMap.put("AddMobileAccess", addMobileAccess);
        ssoUserMap.put("RemoveMobileAccess", removeMobileAccess);
        ssoUserMap.put("AddApplicationAccess", addApplicationAccess);



        String response = HttpUtils.sendPut(ssoProperties.getUpdateUser(), headers, ssoUserMap, propertyFilter);
        logger.debug("update user response: {}", response);
//        try {
//            JSONObject result = JSONObject.parseObject(response);
//            if ("8002".equals(result.get("code").toString())) {
//                logger.info("SSO注册用户成功，userName：{}，userID：{}", user.getUsername(), result.get("userID").toString());
//                return result.get("userID").toString();
//            } else {
//                logger.warn("SSO注册用户失败，userName：{}, msg：{}", user.getUsername(), result.get("errorMessage"));
//            }
//        } catch (Exception e) {
//            logger.warn("SSO注册用户失败，userName：{}", user.getUsername());
//        }
//        return null;
    }

    public static boolean deleteUser(String userId) {
        boolean r = false;

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getSSOTokenFromCookie(SessionUtils.getRequest()));
        Map<String, Object> ssoUserMap = new HashMap<>();
        ssoUserMap.put("id", userId);
        ssoUserMap.put("resource", EnvHolder.getHolder().getResource());
        String response = HttpUtils.sendDel(ssoProperties.getCreateUser(), headers, ssoUserMap);
        try {
            JSONObject result = JSONObject.parseObject(response);
            if ("8004".equals(result.get("code").toString())) {
                logger.info("SSO删除用户成功，userID：{}", userId);
                r = true;
            } else {
                logger.warn("SSO删除用户失败，userID：{}, msg：{}", userId, result.get("errorMessage"));
            }
        } catch (Exception e) {
            logger.warn("SSO删除用户失败，发生未知异常，msg：{}", e.getMessage());
        }
        return r;
    }

    /**
     * 分页搜索用户
     *
     * @param username 查询条件-用户名
     * @return
     */
    public static Pagination<SSOUser> searchPageUser(Integer ps, Integer pi, String username) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + getSSOTokenFromCookie(SessionUtils.getRequest()));

        Map<String, Object> reqBody = new HashMap<>();
        reqBody.put("application", EnvHolder.getHolder().getResource());
        reqBody.put("currentPage", pi);
        reqBody.put("pageSize", ps);
        reqBody.put("userName", username);

        String response = HttpUtils.sendPost(ssoProperties.getSearchPageUser(), headers, reqBody);
        ResponseObject responseObject = parseToResponseObject(response);

        // return null
        if (responseObject == null) {
            return null;
        }
        if (8000 == responseObject.getCode()) {
            // 解析用户列表
            List<SSOUser> userList = Optional.of(responseObject.getData())
                    .map(result -> JSON.parseObject(result.toString()).get("data"))
                    .map(data -> {
                        return JSON.parseArray(data.toString()).toJavaList(SSOUser.class);
                    })
	            .orElse(Collections.emptyList());

            Optional totalCount = Optional.of(responseObject.getData())
                    .map(result -> JSON.parseObject(result.toString()).get("totalCount"));

            return new Pagination<SSOUser>(Integer.parseInt(totalCount.get().toString()), userList);
        } else {
            logger.error("SSO查询用户失败, msg:{}", responseObject.getMsg());
        }
        return null;
    }

    /**
     * 通过eid查找用户详细
     *
     * @param eid
     * @return
     */
    public static SSOUser getUserByEid(String eid) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + EnvHolder.getHolder().getSsoToken());
        String response;
        ResponseObject responseObject = null;
        try {
            response = HttpUtils.sendGet(ssoProperties.getGetUserByEid().replaceAll("\\{eid\\}", eid), headers, null);
            responseObject = parseToResponseObject(response);
            if (responseObject == null) {
                logger.warn("获取用户信息失败：{}", responseObject.getMsg());
                throw new BusinessException(ErrorEnum.SERVER_ERROR);
            } else if (8000 == responseObject.getCode()) {
                SSOUser ssoUser = JSON.toJavaObject(JSON.parseObject(responseObject.getData().toString()), SSOUser.class);
                return ssoUser;
            }
        } catch (Exception e) {
            logger.warn("eid {} is not invalid", eid);
        }

        return null;
    }

    public static ResponseObject parseToResponseObject(String result) {
        if (result == null) {
            return null;
        }
        ResponseObject<Object> responseObject = new ResponseObject<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(result);
            responseObject.setCode(Integer.valueOf(jsonObject.get("code").toString()));
            responseObject.setMsg(jsonObject.get("errorMessage").toString());
            responseObject.setData(jsonObject.get("result"));
        } catch (Exception e) {
            logger.error("SSO返回数据解析失败");
            throw new BusinessException(ErrorEnum.SERVER_ERROR);
        } finally {
        }

        return responseObject;

    }

    public static String getSSOTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SSORestApi.ssoTokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String getSSOTokenFromHeader(HttpServletRequest request) {
        return request.getHeader(ssoTokenName);
    }

    public static String getSSOTokenFromUrl(HttpServletRequest request) {
        return request.getParameter("Authorization");
    }

    public static HttpServletResponse removeToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ssoTokenName.equalsIgnoreCase(cookie.getName())) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return response;
    }
}
