package com.honeywell.fireiot.integrationService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 2:18 PM 6/22/2018
 */
@Slf4j
@Component("webSocket")
public class WebSocketHandle extends TextWebSocketHandler {
//    private static Map<WebSocketSession, User> userSessions = Collections.synchronizedMap(new HashMap<WebSocketSession, User>());

    private static List<WebSocketSession> userSessions = Collections.synchronizedList(new ArrayList<>());
    public WebSocketHandle() {
    }

    public void sendToAll(String message) {
        userSessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        });
    }



    public void sendToArea(String areaId, String message) {

    }

    public void sendWorkOrder(String message, String assignee) {

    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info(session.getId() + "接收到消息");
        log.info("Message Received: " + message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        userSessions.add(session);
        log.info(session.getId() + "上线");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        userSessions.remove(session);
        log.info(session.getId() + "下线");
    }
}
