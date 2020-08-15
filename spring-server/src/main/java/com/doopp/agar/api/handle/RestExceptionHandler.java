package com.doopp.agar.api.handle;

import com.doopp.agar.message.StandardResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    public StandardResponse requestMissingServletRequest(HttpServletResponse response, MissingServletRequestParameterException ex){
        // System.out.println("400..MissingServletRequest");
        // ex.printStackTrace();
        return new StandardResponse<>(ex.getMessage());
    }
}
