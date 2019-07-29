package com.honeywell.fireiot;

import com.honeywell.fireiot.utils.HttpUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/28 7:20 PM
 */
public class SSOTest {

    @Test
    public void testLogin() throws UnsupportedEncodingException {
        SSOEntity entity = new SSOEntity();
        entity.setIp("192.168.1.1");
        entity.setPassword("11111111");
        entity.setUserName("admin");
        String result = HttpUtils.sendPost("http://10.78.115.67:8230/v1.0.0/sso/login", entity);
        System.out.println(result);
    }

    @Test
    public void testVerify(){
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NDYwMDcxMDgsImlhdCI6MTU0NTk5OTkwOCwidXNlcklkIjoiMzkzRTdDM0QtMEEzMS00MENELUE1NjctRUU1REZFOEFCNDkxIiwidXNlck5hbWUiOiJhZG1pbiJ9.3HAa9mWN50Y_sibfgO8IM4mHtOrQVMbokEUq88C5-Jo";
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", "Bearer " + token);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("name", "123");
        String result = HttpUtils.sendGet("http://10.78.115.67:8230/v1.0.0/sso/token/verify", map, paramMap);
        System.out.println(result);
    }
}
