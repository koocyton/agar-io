package com.doopp.iogame.filter;

import com.doopp.gutty.filter.Filter;
import com.doopp.gutty.filter.FilterChain;
import com.google.inject.Singleton;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class WsFilter implements Filter {

    @Override
    public void doFilter(ChannelHandlerContext ctx, FullHttpRequest httpRequest, FullHttpResponse httpResponse, FilterChain filterChain) {

        try {
            String protocol = httpRequest.headers().get("Sec-WebSocket-Protocol");
            if (protocol!=null && protocol.startsWith("User-Token")) {
                httpRequest.headers().add("User-Token", protocol.substring(12));
            }
            filterChain.doFilter(ctx, httpRequest, httpResponse);
        }
        catch (Exception e) {
            e.printStackTrace();
            ctx.channel().writeAndFlush(new TextWebSocketFrame(e.getMessage()));
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            ctx.channel().close();
        }
    }
}
