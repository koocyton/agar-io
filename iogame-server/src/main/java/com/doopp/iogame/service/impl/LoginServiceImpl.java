package com.doopp.iogame.service.impl;

import com.doopp.gutty.annotation.Service;
import com.doopp.gutty.redis.ShardedJedisHelper;
import com.doopp.iogame.pojo.User;
import com.doopp.iogame.pojo.UserToken;
import com.doopp.iogame.service.LoginService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Singleton
@Service
public class LoginServiceImpl implements LoginService {

    @Inject
    @Named("sessionRedis")
    private ShardedJedisHelper sessionRedis;

    final AtomicInteger userId = new AtomicInteger(5163);

    @Override
    public UserToken userRegist(String name) {
        User user = new User(userId.getAndAdd(1), name);
        String token = UUID.randomUUID().toString();
        sessionRedis.set(token.getBytes(), user);
        return new UserToken(token);
    }

    @Override
    public User userLogin(String token) {
        return sessionRedis.get(token.getBytes(), User.class);
    }
}
