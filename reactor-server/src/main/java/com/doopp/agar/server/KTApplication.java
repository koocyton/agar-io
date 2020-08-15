package com.doopp.agar.server;

import com.doopp.agar.server.filter.AppFilter;
import com.doopp.agar.server.json.JacksonMessageConverter;
import com.doopp.agar.server.module.ApplicationModule;
import com.doopp.agar.task.AgarTask;
import com.doopp.reactor.guice.ReactorGuiceServer;
import com.doopp.reactor.guice.redis.RedisModule;
import com.doopp.reactor.guice.redis.ShardedJedisHelper;
import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import redis.clients.jedis.JedisPoolConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KTApplication {

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(new FileInputStream(args[0]));

        String host = properties.getProperty("agar-server.host");
        int port = Integer.parseInt(properties.getProperty("agar-server.port"));

        ReactorGuiceServer.create()
                .bind(host, port)
                .createInjector(
                        binder -> Names.bindProperties(binder, properties),
                        new RedisModule() {
                            @Singleton
                            @Provides
                            public ShardedJedisHelper redisCache(JedisPoolConfig jedisPoolConfig, @Named("agar-server.redis.servers") String userServers) {
                                return new ShardedJedisHelper(userServers, jedisPoolConfig);
                            }
                        },
                        new ApplicationModule()
                )
                .setHttpMessageConverter(new JacksonMessageConverter())
                .basePackages("com.doopp.agar")
                .addResource("/", "/public/")
                .addFilter("/", AppFilter.class)
                .createInjectorAfter(injector->{
                    ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(4);
                    newScheduledThreadPool.scheduleWithFixedDelay(injector.getInstance(AgarTask.class), 5, 5, TimeUnit.SECONDS);
                })
                .printError(true)
                .launch();
    }
}
