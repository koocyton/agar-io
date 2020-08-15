package com.doopp.agar.api.service.impl;

import com.doopp.agar.api.service.UserService;
import com.doopp.agar.pojo.User;
import com.doopp.agar.pojo.UserToken;
import com.doopp.agar.server.util.ShardedJedisHelper;
import com.doopp.agar.util.IdWorker;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private ShardedJedisHelper redisCache;

    @Resource
    private IdWorker idWorker;

    @Override
    public UserToken userLogin(String nickName) {
        UserToken userToken = new UserToken();
        userToken.setUserToken(UUID.randomUUID().toString());
        redisCache.setex(
                userToken.getUserToken().getBytes(),
                3600,
                createUser(nickName)
        );
        return userToken;
    }

    @Override
    public User getUserByToken(String userToken) {
        return redisCache.get(userToken.getBytes(), User.class);
    }

    private User createUser(String nickName) {
        User user = new User();
        user.setId(idWorker.nextId());
        user.setNickname(nickName);
        user.setX(100);
        user.setY(100);
        return user;
    }
}
