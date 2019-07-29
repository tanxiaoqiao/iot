package com.honeywell.fireiot.websocket;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.honeywell.fireiot.dto.UserDto;
import com.honeywell.fireiot.redis.RedisMap;
import com.honeywell.fireiot.redis.RedisMapFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@ServerEndpoint(value = "/polling", configurator = HttpSessionConfigurator.class)
@Slf4j
public class WebSocketServer {

    private static Map<String, Session> webSocketMap = new ConcurrentHashMap<>();

    private static RedisMapFactory redisMapFactory;

    private static RedisMap redisMap;

    @Autowired
    public void setRedisMapFactory(RedisMapFactory redisMapFactory) {
        WebSocketServer.redisMapFactory = redisMapFactory;
        redisMap = RedisMapFactory.getRedisMap("sendmsg");
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        Object currentUser = httpSession.getAttribute("CURRENT_USER");
        log.info("currentUser:=========================" + JSON.toJSONString(currentUser));
        if (currentUser != null) {
            UserDto user = JSONObject.parseObject(currentUser.toString(), UserDto.class);
            Long userId = user.getId();
            webSocketMap.put(userId.toString() + session.getId(), session);
            sendMessageById(userId.toString(), "连接成功");
        }
    }

    @OnClose
    public void onClose(Session session) {
        webSocketMap.entrySet().forEach(k -> {
            String key = k.getKey();
            if (key.indexOf(session.getId()) > -1) {
                webSocketMap.remove(key);
            }
        });


    }

    @OnMessage
    public void onMessage(String message, Session session) {
        sendMessageBySession(session, message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("发生错误： {}, Session ID: {}", throwable.getMessage(), session.getId());
        throwable.printStackTrace();
    }

    public void sendMessageBySession(Session session, String message) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param userId
     * @param message
     */
    public void sendMessageById(String userId, String message) {
        List<Session> collect = new ArrayList<>();
        webSocketMap.entrySet().forEach(s -> {
            log.info("userId id:{}", userId);
            String key = s.getKey();
            if (key.indexOf(userId) > -1) {
                collect.add(webSocketMap.get(key));
            }
        });

        if (CollectionUtils.isEmpty(collect)) {
            return;
        }
        collect.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMessageByUserIds(List<String> userIds, String message) {
        if (!CollectionUtils.isEmpty(userIds)) {
            userIds.forEach(u -> {
                sendMessageById(u, message);
            });
        }
    }

}
