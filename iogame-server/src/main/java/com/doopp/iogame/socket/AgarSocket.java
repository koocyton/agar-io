package com.doopp.iogame.socket;

import com.doopp.gutty.annotation.websocket.*;
import com.doopp.iogame.pojo.Move;
import com.doopp.iogame.pojo.User;
import com.doopp.iogame.proto.message.MessageProto;
import com.doopp.iogame.service.GameService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.log4j.Log4j2;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import java.io.IOException;

@Log4j2
@Socket(subprotocol="User-Token")
@Path("/ws/agar-game")
public class AgarSocket {

    @Inject
    @Named("agarGameService")
    private GameService agarGameService;

    @Open
    public void onConnect(Channel channel, @HeaderParam("User-Token") String userToken) throws IOException {
        agarGameService.onConnect(channel, userToken);
    }

    @TextMessage
    public void onTextMessage(Channel channel, @JsonFrame Move move) {
        agarGameService.receiveMessage(channel, move);
    }

    @Ping
    public void onPing() {
    }

    @Pong
    public void onPong() {
    }

    @Close
    public void onClose(Channel channel) {
        agarGameService.userLeave(channel);
    }
}
