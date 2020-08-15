package com.doopp.agar.test;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.netty.http.client.HttpClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class CommonTest {

    private final static Logger logger = LoggerFactory.getLogger(CommonTest.class);


    @Test
    public void testPhoneSubstr() {
        Long phoneNumber = 18618379089L;
        String phone = phoneNumber.toString();
        String secretPhone = phone.substring(0,3) + "******" + phone.substring(9);
        logger.info("{} : {} : {}", phoneNumber, phone, secretPhone);
    }

    @Test
    public void testPathMatch() {
        String uri = "/2020/05/15/7e79f6cc-7563-400b-8954-2e546b506cf1.jpg";
        int beginIndex = uri.lastIndexOf("-");
        int endIndex = uri.lastIndexOf(".");
        logger.info(uri.substring(0, beginIndex) + uri.substring(endIndex));
        logger.info(uri.substring(beginIndex+1, endIndex));
    }

    @Test
    public void testSet() {
        String[] topics = ",,aaa,bb,cc,dd,ee,ee,".split(",");
        Set<String> topicSet = new HashSet<>(Arrays.asList(topics));
        topicSet.add("ee");
        topicSet.remove("");
        for(String topic : topicSet) {
            logger.info(topic);
        }
    }

    @Test
    public void testDate() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // sf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Calendar c1 = Calendar.getInstance();
        c1.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        c1.add(Calendar.DATE, 0);
        String dateString = sf.format(c1.getTime());
        logger.info(dateString);

        c1.add(Calendar.DATE, -3);
        logger.info(sf.format(c1.getTime()));

        c1.add(Calendar.DATE, 0);
        logger.info(sf.format(c1.getTime()));

        c1.add(Calendar.DATE, -3);
        logger.info(sf.format(c1.getTime()));
    }

    @Test
    public void testCount() {
        for (int ii = 0; ii<8; ii++) {
            // 按 1,2,4,6,8,12,32,64,128,∞ 的天数间隔，拆分所有查询
            int zz = 1 << ii;
            int start = -(2 * zz - 1);
            int end = start + zz;
            logger.info("{} ~ {}", start, end);
        }
    }

    @Test
    public void testList() {
        List<String> sList = new ArrayList<>();
        sList.add(null);
        logger.info(sList.get(0));
    }

    @Test
    public void testWebsocketClient1() throws IOException {
        testWebsocketClient();
    }

    @Test
    public void testWebsocketClient2() throws IOException {
        testWebsocketClient();
    }

    private static void testWebsocketClient() throws IOException {
        //Properties properties = new Properties();
        // properties.load(new FileInputStream("D:\\project\\reactor-guice\\application.properties"));
        //properties.load(new FileInputStream("D:\\project\\agar_io\\application.properties"));

        // int port = Integer.valueOf(properties.getProperty("server.port", "8081"));

        FluxProcessor<String, String> client = ReplayProcessor.<String>create().serialize();

        Flux.interval(Duration.ofMillis(1000))
                .map(Object::toString)
                .subscribe(client::onNext);

        HttpClient.create()
                // .port(port)
                // .wiretap(true)
                .websocket()
                .uri("ws://127.0.0.1:8083/game/ws")
                .handle((in, out) ->
                        out.withConnection(conn -> {
                            in.aggregateFrames().receiveFrames().map(frames -> {
                                if (frames instanceof TextWebSocketFrame) {
                                    System.out.println("Receive text message " + ((TextWebSocketFrame) frames).text());
                                }
                                else if (frames instanceof BinaryWebSocketFrame) {
                                    System.out.println("Receive binary message " + frames.content());
                                }
                                else {
                                    System.out.println("Receive normal message " + frames.content());
                                }
                                return Mono.empty();
                            })
                                    .subscribe();
                        })
                                // .options(NettyPipeline.SendOptions::flushOnEach)
                                .sendString(client)
                )
                .blockLast();
    }
}
