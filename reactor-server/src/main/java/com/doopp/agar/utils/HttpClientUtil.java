package com.doopp.agar.utils;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.Charset;

public class HttpClientUtil {

    private static final HttpClient httpClient = HttpClient.create();

    public static Mono<String> get(String url) {
        return httpClient
                .get()
                .uri(url)
                .responseContent()
                .aggregate()
                .map(byteBuf -> byteBuf.toString(Charset.forName("UTF-8")));
    }
}
