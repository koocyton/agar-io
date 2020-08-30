package com.doopp.agar.handle;

import org.springframework.stereotype.Component;

import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/ws/{what}")
@Component
public class AgarHandle {

    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "name") String what) throws IOException {
        session.getBasicRemote().sendText(what);
    }
}
