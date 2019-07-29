package com.honeywell.fireiot.sso;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSO属性封装
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/29 9:38 AM
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sso")
public class SSOProperties {

    private String url;
    private boolean enable;

    private String login;
    private String verifyToken;
    private String logout;
    private String checkUserName;
    private String searchPageUser;
    private String changePassword;

    private String createUser;
    private String deleteUser;
    private String updateUser;
    private String getUserByEid;
}
