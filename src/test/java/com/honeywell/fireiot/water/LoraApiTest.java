package com.honeywell.fireiot.water;

import com.alibaba.fastjson.JSON;
import com.honeywell.fireiot.water.schedule.bean.LoraApiLoginRequest;
import com.honeywell.fireiot.water.schedule.bean.LoraApiLoginResult;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: xiaomingCao
 * @date: 2018/12/28
 */
public class LoraApiTest {

    RestTemplate restTemplate = new RestTemplate();


    @Test
    public void queryTest(){

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "bearer " + loginTest());


        Map<String,Object> data = new HashMap<>();
        data.put("confirmed", true);
        data.put("fPort", 220);
        data.put("deveui", "ffffff1000004305");
        data.put("data", "/kRRUw0K");

        HttpEntity entity = new HttpEntity(data, headers);

        ResponseEntity<String> res = restTemplate.exchange(
                "http://10.78.115.175:8000/api/v1/nodes/{eui}/queue",
                HttpMethod.POST,
                entity,
                String.class,
                "ffffff1000004305"
        );
        System.out.println(res.getBody());
    }


    @Test
    public void logiTest(){
        LoraApiLoginRequest request = new LoraApiLoginRequest();
        request.setPassword("admin");
        request.setPassword("admin");
        ResponseEntity<LoraApiLoginResult> res = restTemplate
                .postForEntity("http://10.78.115.175:8000/api/v1/user/login", request, LoraApiLoginResult.class);
        System.out.println(JSON.toJSONString(res.getBody()));
    }


    public String loginTest(){
        Map<String, Object> data = new HashMap<>();
        data.put("username", "admin");
        data.put("password", "admin");
        ResponseEntity<String> res = restTemplate
                .postForEntity("http://10.78.115.175:8000/api/v1/user/login", data, String.class);
        HashMap hashMap = JSON.parseObject(res.getBody(), HashMap.class);
        return String.valueOf(hashMap.get("jwt"));
    }

}
