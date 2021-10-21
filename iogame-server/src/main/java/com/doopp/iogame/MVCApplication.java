package com.doopp.iogame;

import com.doopp.gutty.Gutty;
import com.doopp.gutty.json.JacksonMessageConverter;
import com.doopp.gutty.redis.*;
import com.doopp.iogame.filter.WsFilter;
import com.doopp.iogame.task.AgarGameTask;
import com.doopp.iogame.util.IdWorker;
import com.google.inject.*;
import com.google.inject.name.Named;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import com.google.inject.Module;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class MVCApplication {

    public static void main(String[] args) {
        new Gutty()
            .loadProperties(args)
            .setBasePackages("com.doopp.iogame")
            .setMessageConverter(JacksonMessageConverter.class)
            .addFilter("/api", WsFilter.class)
            .addFilter("/ws", WsFilter.class)
            .addModules(
                new Module() {
                    @Override
                    public void configure(Binder binder) {
                    }

                    @Singleton
                    @Provides
                    @Named("bossEventLoopGroup")
                    public EventLoopGroup bossEventLoopGroup() {
                        return new NioEventLoopGroup();
                    }

                    @Singleton
                    @Provides
                    @Named("workerEventLoopGroup")
                    public EventLoopGroup workerEventLoopGroup() {
                        return new NioEventLoopGroup();
                    }

                    @Singleton
                    @Provides
                    public IdWorker idWorker() {
                        return new IdWorker(1, 1);
                    }
                },
                new RedisModule() {
                    @Override
                    protected void initialize() {
                        bindShardedJedisPoolConfigProvider(ShardedJedisPoolConfigProvider.class);
                        bindSerializableHelper(JdkSerializableHelper.class);
                    }
                    @Singleton
                    @Provides
                    @Named("sessionRedis")
                    public ShardedJedisHelper userRedis(ShardedJedisPoolConfig jedisConfig, SerializableHelper serializableHelper, @Named("redis.session.servers") String userServers) {
                        return new ShardedJedisHelper(userServers, jedisConfig, serializableHelper);
                    }
                }
            )
            .addInjectorConsumer(injector->{
                // GameService agarGameService = injector.getInstance(Key.get(AgarGameServiceImpl.class));
                // agarGameService.runDaemon();
                ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(4);
                newScheduledThreadPool.scheduleWithFixedDelay(injector.getInstance(AgarGameTask.class), 4, 10, TimeUnit.MILLISECONDS);
            })
            .start();
    }
}
