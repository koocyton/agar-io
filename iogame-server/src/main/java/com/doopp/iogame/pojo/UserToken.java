package com.doopp.iogame.pojo;

import lombok.Data;

@Data
public class UserToken {

    private String userToken;

    public UserToken(String userToken) {
        this.userToken = userToken;
    }
}
