package com.honeywell.fireiot.utils;

import com.honeywell.fireiot.security.WebSecurityConfig;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2019/4/18 2:03 PM
 */
public class SecurityUtil {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    public static boolean isAnonymousUrl(HttpServletRequest request) {

        if (SessionUtils.getRequest() == null) {
            return false;
        }

        String path = request.getServletPath();
        if (StringUtils.isEmpty(path)) {
            return false;
        }

        if (WebSecurityConfig.ignoreUrl != null) {
            String[] ignoreUrl = WebSecurityConfig.ignoreUrl.split(",");
            for (String pattern : ignoreUrl) {
                if (MATCHER.match(pattern, path)) {
                    return true;
                }
            }
        }

        if (WebSecurityConfig.anonymousUrl != null) {
            String[] anonymousUrl = WebSecurityConfig.anonymousUrl.split(",");
            for (String pattern : anonymousUrl) {
                if (MATCHER.match(pattern, path)) {
                    return true;
                }
            }
        }
        return false;
    }
}
