package com.doopp.agar.api.service;

import com.doopp.agar.pojo.User;
import com.doopp.agar.pojo.UserToken;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<UserToken> userLogin(String nickName);

    Mono<User> getUserByToken(String userToken);
}
