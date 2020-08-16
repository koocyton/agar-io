package com.doopp.agar.handle;

import com.doopp.agar.pojo.User;
import com.doopp.agar.utils.JsonUtil;
import com.doopp.reactor.guice.RequestAttribute;
import com.doopp.reactor.guice.websocket.AbstractWebSocketServerHandle;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.ws.rs.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Path("/ws/agar-io")
@Slf4j
@Singleton
public class AgarHandle extends AbstractWebSocketServerHandle {

    private final Map<Long, User> players = new ConcurrentHashMap<>();

    private final Map<Long, Channel> channels = new ConcurrentHashMap<>();

    private final AttributeKey<Long> userIdAttributeKey = AttributeKey.valueOf("UserId");


    @Inject
    private JsonUtil jsonUtil;

    @Override
    public String secWebSocketProtocol(HttpServerRequest request) {
        return "User-Token";
    }

    @Override
    public Mono<Void> onConnect(Channel channel) {
        RequestAttribute requestAttribute = channel.attr(RequestAttribute.REQUEST_ATTRIBUTE).get();
        User user = requestAttribute.getAttribute("SessionUser", User.class);
        user.setAction("move");
        addPlayer(channel, user);
        return Mono.empty();
    }

    @Override
    protected Mono<Void> onTextMessage(TextWebSocketFrame frame, Channel channel) {
        User user = getChannelUser(channel);
        if (user!=null && !Strings.isNullOrEmpty(frame.text()) && frame.text().contains(" ")) {
            String[] xy = frame.text().split(" ");
            user.setX(Float.parseFloat(xy[0]));
            user.setY(Float.parseFloat(xy[1]));
            players.put(user.getId(), user);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> onClose(CloseWebSocketFrame frame, Channel channel) {
        removePlayer(channel);
        return super.onClose(frame, channel);
    }

    public void pushPlayers() {
        String playersString = playersToString();
        channels.forEach((userId, channel)->{
            if (channel.isOpen()) {
                this.sendTextMessage(playersString, channel);
            }
        });
    }

    private String playersToString() {
        AtomicReference<String> s = new AtomicReference<>("");
        players.forEach((userId, user)->{
            s.set(s.get()+"\n"+user.toString());
        });
        return s.get();
    }

    private Long getChannelUserId(Channel channel) {
        return channel.attr(userIdAttributeKey).get();
    }

    private void setChannelUser(Channel channel, User user) {
        channel.attr(userIdAttributeKey).set(user.getId());
    }

    private User getChannelUser(Channel channel) {
        Long userId = getChannelUserId(channel);
        if (userId==null) {
            return null;
        }
        return players.get(userId);
    }

    private void addPlayer(Channel channel, User user) {
        players.put(user.getId(), user);
        channels.put(user.getId(), channel);
        setChannelUser(channel, user);
    }

    private void removePlayer(Channel channel) {
        Long userId = getChannelUserId(channel);
        if (userId!=null) {
            players.remove(userId);
            channels.remove(userId);
        }
    }
}
