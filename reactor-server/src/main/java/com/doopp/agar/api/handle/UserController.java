package com.doopp.agar.api.handle;

import com.doopp.agar.api.service.UserService;
import com.doopp.agar.define.StatusCode;
import com.doopp.agar.message.StandardException;
import com.doopp.agar.pojo.User;
import com.doopp.agar.pojo.UserToken;
import com.doopp.reactor.guice.annotation.Controller;
import com.doopp.reactor.guice.annotation.RequestAttribute;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Slf4j
@Controller
@Path("/api")
public class UserController {

    @Inject
    private UserService userService;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<UserToken> login(@BeanParam User user) {
        if (user==null || Strings.isNullOrEmpty(user.getNickname())) {
            return Mono.error(
                    new StandardException(StatusCode.NICK_NOT_EMPTY)
            );
        }
        return userService.userLogin(user.getNickname());
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Mono<User> me(@RequestAttribute("SessionUser") User me) {
        if (me==null) {
            return Mono.error(
                new StandardException(StatusCode.USER_NOT_FOUND)
            );
        }
        return Mono.just(me);
    }
}
