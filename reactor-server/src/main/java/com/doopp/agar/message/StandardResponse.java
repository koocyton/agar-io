package com.doopp.agar.message;

import lombok.Data;

@Data
public class StandardResponse<T> {

    //@ApiModelProperty(value="状态码，0 标识正常", example = "0")
    private int code = 0;

    // @ApiModelProperty(value="错误消息，空标识正常", example = "")
    private String msg = "";

    private T data;

    // 没有他 jackson 就不解析，蛋疼不
    public StandardResponse() {
    }

    public StandardResponse(T data) {
        this.data = data;
    }

    public StandardResponse(String msg) {
        this.code = 0;
        this.msg = msg;
    }
}
