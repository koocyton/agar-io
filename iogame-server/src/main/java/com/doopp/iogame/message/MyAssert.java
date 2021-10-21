package com.doopp.iogame.message;


import com.doopp.iogame.util.VerifyUtil;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

public abstract class MyAssert {

    public static void notNull(@Nullable Object object, MyError myError) {
        if (object == null) {
            throw new MyException(myError);
        }
    }

    public static void notEmpty(@Nullable Object object, MyError myError) {
        if (VerifyUtil.isEmpty(object)) {
            throw new MyException(myError);
        }
    }

    private final static String r = "[a-zA-Z0-9`~!@#\\$%\\^&\\*\\(\\)-_=\\+\\[\\{\\]\\}\\\\\\|;:'\",<\\.>/\\?]{8,26}";
    public static void isPassword(String password, MyError myError) {
        if (!Pattern.matches(r, password)) {
            throw new MyException(myError);
        }
    }

    public static void needTrue(boolean state, MyError myError) {
        if (!state) {
            throw new MyException(myError);
        }
    }

    public static void isEmail(String email, MyError myError) {
        if (!VerifyUtil.isEmail(email)) {
            throw new MyException(myError);
        }
    }
}
