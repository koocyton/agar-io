package com.doopp.agar.message;

import com.doopp.agar.define.StatusCode;

public class StandardException extends RuntimeException {

    private int code = 0;

    public StandardException(String errorMessage) {
        super(errorMessage);
        this.code = StatusCode.FAIL.code();
    }

    public StandardException(StatusCode commonError) {
        super(commonError.message());
        this.code = commonError.code();
    }

    public StandardException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.code = errorCode;
    }

    public static void notEmpty(Object obj, StatusCode statusCode) {
        if (obj==null) {
            throw new StandardException(statusCode);
        }
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return super.getMessage();
    }

    //编写一个泛型方法对异常进行包装
    public static <E extends Exception> void doThrow(Exception e) throws E {
        throw (E)e;
    }
}
