package com.sam.wiki.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 每個客戶端一個token
     */
    private String token = "";

    private static HashMap<String, Session> map = new HashMap<>();

    /**
     * 連接成功
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        map.put(token, session);
        this.token = token;
        LOG.info("有新連接：token：{}，session id：{}，當前連接數：{}", token, session.getId(), map.size());
    }

    /**
     * 連接關閉
     */
    @OnClose
    public void onClose(Session session) {
        map.remove(this.token);
        LOG.info("連接關閉，token：{}，session id：{}！當前連接數：{}", this.token, session.getId(), map.size());
    }

    /**
     * 收到消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.info("收到消息：{}，內容：{}", token, message);
    }

    /**
     * 連接錯誤
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOG.error("發生錯誤", error);
    }

    /**
     * 群發消息
     */
    public void sendInfo(String message) {
        for (String token : map.keySet()) {
            Session session = map.get(token);
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOG.error("推送消息失敗：{}，內容：{}", token, message);
            }
            LOG.info("推送消息：{}，內容：{}", token, message);
        }
    }

}
