package com.doopp.iogame.message;

public class MyException extends RuntimeException {

    private int code = 0;

    public int getCode() {
        return code;
    }

    public MyException(String errorMessage) {
        super(errorMessage);
        this.code = MyError.FAIL.code();
    }

    public MyException(Exception e) {
        super(e.getMessage());
        this.code = 500;
    }

    public MyException(MyError commonError) {
        super(commonError.message());
        this.code = commonError.code();
    }

    public MyException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.code = errorCode;
    }
}
