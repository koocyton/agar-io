package com.doopp.iogame.service;

import com.doopp.iogame.pojo.User;
import com.doopp.iogame.pojo.UserToken;

public interface LoginService {

    User userLogin(String token);

    UserToken userRegist(String name);
}
