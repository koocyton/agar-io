package com.doopp.agar.api.service.impl;

import com.doopp.agar.api.service.UserService;
import com.doopp.agar.pojo.Food;
import com.doopp.agar.pojo.User;
import com.doopp.agar.pojo.UserToken;
import com.doopp.agar.utils.IdWorker;
import com.doopp.reactor.guice.annotation.Service;
import com.doopp.reactor.guice.redis.ShardedJedisHelper;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Inject
    private ShardedJedisHelper redisCache;

    @Inject
    private IdWorker idWorker;

    @Override
    public Mono<UserToken> userLogin(String nickName) {
        return Mono.just(createToken())
                .map(userToken -> {
                    redisCache.setex(userToken.getUserToken().getBytes(), 3600, createUser(nickName));
                    return userToken;
                });
    }

    @Override
    public Mono<User> getUserByToken(String userToken) {
        return Mono.just(
                redisCache.get(userToken.getBytes(), User.class)
        );
    }

    private UserToken createToken() {
        UserToken userToken = new UserToken();
        userToken.setUserToken(UUID.randomUUID().toString());
        return userToken;
    }

    private User createUser(String name) {
        int color = (int)(Math.random() * 0x1000000);
        String rgb = Integer.toHexString(color%256)
                + Integer.toHexString(color/256%256)
                + Integer.toHexString(color/256/256%256);
        User user = new User();
        user.setId(idWorker.nextId());
        user.setName(name);
        user.setType("cell");
        user.setGrade(4000);
        user.setColor(rgb);
        user.setX((int)(Math.random() * 0x1500));
        user.setY((int)(Math.random() * 0x1500));
        user.setTime(System.currentTimeMillis());
        return user;
    }

    @Override
    public Food creatFood() {
        int color = (int)(Math.random() * 0x1000000);
        String rgb = Integer.toHexString(color%256)
                + Integer.toHexString(color/256%256)
                + Integer.toHexString(color/256/256%256);
        Food food = new Food();
        food.setId(idWorker.nextId());
        food.setType("food");
        food.setGrade(1000);
        food.setColor(rgb);
        food.setX((int)(Math.random() * 0x1500));
        food.setY((int)(Math.random() * 0x1500));
        return food;
    }
}
