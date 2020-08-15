package com.doopp.agar.message;

public class StandardResponse<T> {

    private int code = 0;

    private String msg = "";

    private T data;

    public StandardResponse(T data) {
        this.data = data;
    }

    public StandardResponse(String msg) {
        this.code = 0;
        this.msg = msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
