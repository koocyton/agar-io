package com.doopp.agar.api.controller;
import com.doopp.agar.api.service.UserService;
import com.doopp.agar.define.StatusCode;
import com.doopp.agar.message.StandardException;
import com.doopp.agar.message.StandardResponse;
import com.doopp.agar.pojo.User;
import com.doopp.agar.pojo.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@Controller
@RequestMapping("/api")
public class UserController {

    @Resource
    private UserService userService;

    @ResponseBody
    @PostMapping("/login")
    public StandardResponse<UserToken> login(@RequestBody User user) {
        if (user==null || StringUtils.isEmpty(user.getNickname())) {
            throw new StandardException(StatusCode.NICK_NOT_EMPTY);
        }
        UserToken userToken = userService.userLogin(user.getNickname());
        return new StandardResponse<>(userToken);
    }

    @ResponseBody
    @RequestMapping("/me")
    public StandardResponse<User> me(@SessionAttribute("SessionUser") User me) {
        if (me==null) {
            throw new StandardException(StatusCode.USER_NOT_FOUND);
        }
        return new StandardResponse<>(me);
    }
}
