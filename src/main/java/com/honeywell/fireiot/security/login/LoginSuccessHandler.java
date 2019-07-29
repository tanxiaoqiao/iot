/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.honeywell.fireiot.security.login;

import com.alibaba.fastjson.JSON;
import com.honeywell.fireiot.security.entity.AuthorityUser;
import com.honeywell.fireiot.utils.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * Forward Authentication Success Handler
 * </p>
 *
 * @author Shazin Sadakath
 * @since 4.1
 */
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger("security");

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AuthorityUser currentUser = (AuthorityUser) authentication.getPrincipal();
//        AuthorityUser currentUser = BusinessUtils.getSecurityUser();
        logger.info("Login success: " + currentUser.getUsername());

        // 返回当前用户信息
        response.setContentType("application/json;charset=utf-8");
        currentUser.setPassword(null);
        response.getWriter().write(JSON.toJSONString(ResponseObject.success(currentUser)));
    }
}
