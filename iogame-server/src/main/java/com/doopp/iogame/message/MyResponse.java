package com.doopp.iogame.message;

public class MyResponse<T> {

    private int code = 0;

    private String msg = "";

    private T data;

    // 没有他 jackson 就不解析，蛋疼不
    public MyResponse() {
    }

    public static <Z> MyResponse<Z> ok(Z data) {
        return new MyResponse<Z>(data, 0, "");
    }

    public static <Z> MyResponse<Z> error(String msg) {
        return new MyResponse<Z>(null, 500, msg);
    }

    public MyResponse(T data, int code, String msg) {
        this.data = data;
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
