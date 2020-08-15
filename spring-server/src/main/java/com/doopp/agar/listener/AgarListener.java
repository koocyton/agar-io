package com.doopp.agar.listener;

import com.doopp.agar.api.service.UserService;
import com.doopp.agar.pojo.User;
import com.doopp.agar.util.JsonUtil;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

@Slf4j
@Component("agarListener")
public class AgarListener extends AbstractReceiveListener {

    private static final Pattern PATTERN = Pattern.compile("\\s*,\\s*");

    @Resource
    private UserService userService;

    @Resource
    private JsonUtil jsonUtil;

    private AtomicBoolean atomicBoolean = new AtomicBoolean(true);

    private final Map<Long, User> players = new ConcurrentHashMap<>();

    private final Map<Long, WebSocketChannel> channels = new ConcurrentHashMap<>();

    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        User user = onConnectUser(exchange);
        if (user==null) {
            if (channel.isOpen()) {
                WebSockets.sendText("Hi, who are you ?", channel, null);
                channelClose(channel);
            }
            return;
        }
        addPlayer(channel, user);
        WebSockets.sendText("Welcome, " + user.getNickname(), channel, null);
    }

    @Override
    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
        User user = jsonUtil.toObject(message.getData(), User.class);
        if (atomicBoolean.get()) {
            atomicBoolean.set(false);
            channels.forEach((id, ch) -> {
                WebSockets.sendText(jsonUtil.toJsonString(user), ch, null);
            });
            atomicBoolean.set(true);
        }
    }

    @Override
    protected void onClose(WebSocketChannel channel, StreamSourceFrameChannel frameChannel) {
        removePlayer(channel);
        if (channel.isOpen()) {
            try {
                channel.close();
            }
            catch (IOException ignore) {}
        }
    }

    private Long getChannelUserId(WebSocketChannel channel) {
        Object userId = channel.getAttribute("UserId");
        if (userId==null) {
            return null;
        }
        return (Long) userId;
    }

    private void setChannelUser(WebSocketChannel channel, User user) {
        channel.setAttribute("UserId", user.getId());
    }

    private User getChannelUser(WebSocketChannel channel) {
        Long userId = getChannelUserId(channel);
        if (userId==null) {
            return null;
        }
        return players.get(userId);
    }

    private void addPlayer(WebSocketChannel channel, User user) {
        players.put(user.getId(), user);
        channels.put(user.getId(), channel);
        setChannelUser(channel, user);
    }

    private void removePlayer(WebSocketChannel channel) {
        Long userId = getChannelUserId(channel);
        if (userId!=null) {
            players.remove(userId);
            channels.remove(userId);
        }
    }

    private User onConnectUser(WebSocketHttpExchange exchange) {
        String requestedSubprotocols = exchange.getRequestHeader("Sec-WebSocket-Protocol");
        if (requestedSubprotocols==null) {
            return null;
        }
        String[] requestedSubprotocolArray = PATTERN.split(requestedSubprotocols);
        if (requestedSubprotocolArray.length<1) {
            return null;
        }
        for (String p : requestedSubprotocolArray) {
            if (p.length() > 30) {
                return userService.getUserByToken(p);
            }
        }
        return null;
    }

    private void channelClose(WebSocketChannel channel) {
        try {
            channel.close();
        }
        catch (IOException ignore) {
        }
    }
}
