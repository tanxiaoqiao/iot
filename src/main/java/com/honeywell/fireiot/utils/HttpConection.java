package com.honeywell.fireiot.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Component
public class HttpConection {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpConection.class);


    public static String pushMsg(String auth, RequestBody requestBody, String pushurl) {
        // 用来存储响应数据
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(pushurl);
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);

            // 设置连接输入流为true
            connection.setDoInput(true);

            // 设置请求方式为post
            connection.setRequestMethod("POST");

            // post请求缓存设为false
            connection.setUseCaches(false);

            // 设置该HttpURLConnection实例是否自动执行重定向
            connection.setInstanceFollowRedirects(true);

            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            //鉴权方式
            connection.setRequestProperty("Authorization", auth);

            // 建立连接 (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();

            // 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
            DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());

            String params = JSONObject.toJSONString(requestBody);
            LOGGER.info("params:{}", params);
            dataout.writeBytes(params);

            // 输出完成后刷新并关闭流
            dataout.flush();
            // 重要且易忽略步骤 (关闭流,切记!)
            dataout.close();
            // 连接发起请求,处理服务器响应  (从连接获取到输入流并包装为bufferedReader)
            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;

            // 循环读取流,若不到结尾处
            while ((line = bf.readLine()) != null) {
                // sb.append(bf.readLine());
                sb.append(line);
            }
            // 重要且易忽略步骤 (关闭流,切记!)
            bf.close();
            // 销毁连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("responseMsg:{}", sb.toString());
        return sb.toString();
    }

}