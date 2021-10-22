package com.doopp.iogame.controller;

import com.doopp.gutty.annotation.Controller;
import com.doopp.iogame.message.MyResponse;
import com.doopp.iogame.pojo.User;
import com.doopp.iogame.pojo.UserToken;
import com.doopp.iogame.service.GameService;
import com.doopp.iogame.service.LoginService;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.extern.log4j.Log4j2;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Log4j2
@Controller
@Path("/api")
public class LoginController {

    @Inject
    private LoginService loginService;

    @Inject
    @Named("agarGameService")
    private GameService agarGameService;

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public MyResponse<UserToken> register(User user) {
        return MyResponse.ok(
                loginService.userRegist(user.name)
        );
    }

    @GET
    @Path("/online-users")
    @Produces(MediaType.APPLICATION_JSON)
    public MyResponse<Map<Integer, User>> onlineUsers() {
        return MyResponse.ok(
                agarGameService.getUserMap()
        );
    }
}
