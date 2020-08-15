package com.doopp.agar.api.service;

import com.doopp.agar.pojo.User;
import com.doopp.agar.pojo.UserToken;

public interface UserService {

    UserToken userLogin(String nickName);

    User getUserByToken(String userToken);
}
