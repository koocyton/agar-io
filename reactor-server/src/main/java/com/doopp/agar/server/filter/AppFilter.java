package com.doopp.agar.server.filter;

import com.doopp.agar.api.service.UserService;
import com.doopp.reactor.guice.Filter;
import com.doopp.reactor.guice.RequestAttribute;
import com.doopp.reactor.guice.StatusMessageException;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.net.URI;
import java.util.regex.Pattern;

@Slf4j
public class AppFilter implements Filter {

    private static final Pattern PATTERN = Pattern.compile("\\s*,\\s*");

    @Inject
    private UserService userService;

    @Override
    public Mono<Object> doFilter(HttpServerRequest request, HttpServerResponse response, RequestAttribute requestAttribute) {

        String uri = URI.create(request.uri()).getPath();

        // 不过滤的uri
        String[] notNeedFilters = new String[]{
                "/api/login",
        };

        // 是否过滤此 URL
        boolean needFilter = isNeedFilter(uri, notNeedFilters);

        // 根路径
        if (uri.equals("") || uri.equals("/")) {
            needFilter = false;
        }

        // 如果需要过滤
        if (needFilter) {
            String userToken = request.requestHeaders().get("User-Token");
            String headerProtocol = request.requestHeaders().get("Sec-WebSocket-Protocol");
            if (headerProtocol!=null && !Strings.isNullOrEmpty(headerProtocol)) {
                String[] protocolArray = PATTERN.split(headerProtocol);
                for (String p : protocolArray) {
                    if (p.length()>32) {
                        userToken = p;
                    }
                }
            }
            return userService.getUserByToken(userToken)
                .switchIfEmpty(
                    Mono.error(new StatusMessageException(HttpResponseStatus.NOT_FOUND.code(), "user not fount"))
                )
                .map(user->{
                    requestAttribute.setAttribute("SessionUser", user);
                    return requestAttribute;
                });
        }

        // 不需要过滤
        return Mono.just(requestAttribute);
    }
}
