package com.honeywell.fireiot.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Http请求工具类
 *
 * @Author: zhenzhong.wang@honeywell.com
 * @Date: 2018/12/28 3:57 PM
 */
@Component
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);


    private static final RequestConfig DEFAULT_CONFIG;

    private static String pushUrl;

    @Value("${push.url}")
    public void setPushUrl(String pushUrl) {
        HttpUtils.pushUrl = pushUrl;
    }

    public static String getPushUrl() {
        return pushUrl;
    }

    static {
        DEFAULT_CONFIG = RequestConfig.custom().
                setSocketTimeout(10000).
                setConnectTimeout(10000).
                setConnectionRequestTimeout(10000).
                build();
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param object
     * @return
     */
    public static String sendPost(String url, Object object) {
        return sendPost(url, null, object);
    }

    /**
     * 发送POST请求
     *
     * @param url
     * @param object
     * @return
     */
    public static String sendPost(String url, Map<String, String> headers, Object object) {

        String content = (object == null ? "" : JSONObject.toJSONString(object));

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        if (headers != null) {
            headers.forEach((key, value) -> {
                httpPost.setHeader(key, value);
            });
        }
        StringEntity requestEntity = new StringEntity(content, Consts.UTF_8);
        httpPost.setEntity(requestEntity);
        httpPost.setConfig(DEFAULT_CONFIG);

        CloseableHttpResponse response = null;
        try {
            logger.debug("Request URL: " + url);
            logger.debug("Request Content: " + content);
            response = getClient().execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO:请求失败处理
//        if (response.getStatusLine().getStatusCode() != 200) {
//
//        }

        return parseResult(response.getEntity());
    }


    public static String sendGet(String url) {
        return sendGet(url, null, null);
    }

    /**
     * 发送GET请求
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    public static String sendGet(String url, Map<String, String> headers, Map<String, String> params) {
        // 初始化请求参数
        String serializeParameter = paramsSerialize(params);
        if (StringUtils.isNotEmpty(serializeParameter)) {
            url = url + "?" + serializeParameter;
        }
        HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Content-Type", "application/json");
        httpGet.setConfig(DEFAULT_CONFIG);
        if (headers != null) {
            headers.forEach((key, value) -> {
                httpGet.setHeader(key, value);
            });
        }
        CloseableHttpResponse response = null;
        try {
            logger.debug("Request URL: " + url);
            response = getClient().execute(httpGet);
        } catch (NoHttpResponseException e) {
            logger.warn("请求SSO异常，可能出现在校验eid时");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null) {
            logger.warn("请求SSO异常");
            return null;
        }
        return parseResult(response.getEntity());
    }


    /**
     * 发送Put请求
     *
     * @param url
     * @param object
     * @return
     */
    public static String sendPut(String url, Map<String, String> headers, Object object) {

        return sendPut(url, headers, object, null);
    }

    /**
     * 发送Put请求
     *
     * @param url
     * @param object
     * @return
     */
    public static String sendPut(String url, Map<String, String> headers, Object object, PropertyFilter filter) {
        String content;
        if (filter == null) {
            content = (object == null ? "" : JSONObject.toJSONString(object));
        } else {
            content = (object == null ? "" : JSONObject.toJSONString(object, filter));
        }

        HttpPut httpPut = new HttpPut(url);
        httpPut.setHeader("Content-Type", "application/json");
        if (headers != null) {
            headers.forEach((key, value) -> {
                httpPut.setHeader(key, value);
            });
        }
        StringEntity requestEntity = new StringEntity(content, Consts.UTF_8);
        httpPut.setEntity(requestEntity);
        httpPut.setConfig(DEFAULT_CONFIG);

        CloseableHttpResponse response = null;
        try {
            logger.debug("Request URL: " + url);
            logger.debug("Request Content: " + content);
            response = getClient().execute(httpPut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO:请求失败处理
//        if (response.getStatusLine().getStatusCode() != 200) {
//
//        }

        return parseResult(response.getEntity());
    }

    /**
     * 发送Delete请求
     *
     * @param url
     * @param object
     * @return
     */
    public static String sendDel(String url, Map<String, String> headers, Object object) {

        String content = (object == null ? "" : JSONObject.toJSONString(object));

        HttpDeleteWithBody httpDel = new HttpDeleteWithBody(url);
        httpDel.setHeader("Content-Type", "application/json");
        if (headers != null) {
            headers.forEach((key, value) -> {
                httpDel.setHeader(key, value);
            });
        }
        StringEntity requestEntity = new StringEntity(content, Consts.UTF_8);

        httpDel.setConfig(DEFAULT_CONFIG);
        httpDel.setEntity(requestEntity);

        CloseableHttpResponse response = null;
        try {
            logger.debug("Request URL: " + url);
            logger.debug("Request Content: " + content);
            response = getClient().execute(httpDel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO:请求失败处理
//        if (response.getStatusLine().getStatusCode() != 200) {
//
//        }

        return parseResult(response.getEntity());
    }


    public static String parseResult(HttpEntity entity) {
        String result = null;
        try {
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取client
     *
     * @return
     */
    public static CloseableHttpClient getClient() {
        // 采用默认方式获取client，默认方式会通过连接池建立连接
        CloseableHttpClient client = HttpClients.createDefault();
        return client;
    }

    /**
     * 请求参数序列化
     *
     * @param params
     * @return
     */
    public static String paramsSerialize(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        String formStr = "";
        for (String key : params.keySet()) {
            formStr += key + "=" + params.get(key) + "&";
        }
        // 去除多余的&符
        if (formStr.lastIndexOf('&') == (formStr.length() - 1)) {
            formStr = formStr.substring(0, formStr.length() - 1);
        }
        return formStr;
    }
}
