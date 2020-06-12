package com.ro.learn.push;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint(value = "/push")
public class PushEndPoint {
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        session.getAsyncRemote().sendObject("Welcome " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        session.getAsyncRemote().sendObject("Welcome " + session.getId() + " " + message);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }
}
