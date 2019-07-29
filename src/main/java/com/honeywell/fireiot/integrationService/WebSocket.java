package com.honeywell.fireiot.integrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 4:30 PM 6/20/2018
 */
//@Component
//@ServerEndpoint(value = "/websocket")
public class WebSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocket.class);

    private static Set<Session> userSessions = Collections.synchronizedSet(new HashSet<>());

    public static void sendMessageAll(String message) throws IOException {
        userSessions.forEach(session -> {
            if(session.isOpen()){
                session.getAsyncRemote().sendText(message);
            }
        });
    }

    public static void sendMessageTo(Session tosession, String message) throws IOException {
        if (tosession.isOpen()) {
            tosession.getAsyncRemote().sendText(message);
        }
    }

    public static Set<Session> getUserSessions() {
        return userSessions;
    }

    @OnOpen
    public void onOpen(Session userSession) {
        LOGGER.info(userSession.getId() + "上线");
        userSessions.add(userSession);
    }

    @OnClose
    public void onClose(Session userSession) {
        LOGGER.info(userSession.getId() + "下线");
        userSessions.remove(userSession);
    }

    @OnMessage
    public void onMessage(String message, Session userSession) {
        LOGGER.info("Message Received: " + message);
    }

}
