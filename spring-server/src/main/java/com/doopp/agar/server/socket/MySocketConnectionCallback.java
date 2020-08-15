package com.doopp.agar.server.socket;

import com.doopp.agar.listener.AgarListener;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class MySocketConnectionCallback implements WebSocketConnectionCallback {

    @Resource
    private AgarListener agarListener;

    @SneakyThrows
    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel)
    {
        agarListener.onConnect(exchange, channel);
        channel.getReceiveSetter().set(agarListener);
        channel.resumeReceives();
    }
}
