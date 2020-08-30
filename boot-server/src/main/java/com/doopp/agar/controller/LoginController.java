package com.doopp.agar.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api")
@RestController
public class LoginController {

    @ResponseBody
    @RequestMapping("/login")
    public Map<String, Object> userLogin() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("aa", "bb");
        return resp;
    }
}
