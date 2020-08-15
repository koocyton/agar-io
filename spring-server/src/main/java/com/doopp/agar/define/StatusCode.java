package com.doopp.agar.define;

public enum StatusCode {

    SUCCESS(0, ""),
    ACCOUNT_NO_EXIST(401, "账号不存在"),

    ACCOUNT_LENGTH_UNQUALIFIED(419, "Account length is between 7 and 20 characters"),
    PASSWORD_LENGTH_UNQUALIFIED(420, "Password length is between 10 and 30 characters"),
    REPEAT_PASSWORD_FAILED(421, "Two inconsistent passwords"),
    EMAIL_FORMAT_ERROR(422, "Error in mail format"),
    MOBILE_FORMAT_ERROR(423, "Error in mobile format"),
    CAPTCHA_NOT_EMPTY(424, "Input captcha cannot be empty"),
    SM_CODE_NOT_EMPTY(425, "Input SM code cannot be empty"),
    ACCOUNT_FORMAT_FAILED(426, "Please login with the correct email, cell phone number, or your account number."),

    FAIL(500, "服务端异常"),
    PASSWORD_INCORRECT(501, "密码错误"),
    MANAGER_NO_LOGIN(502, "manager not login"),
    WRONG_SESSION(503, "错误的会话"),
    EXPIRE_TIME(504, "expire time"),
    CLIENT_FAILED(505, "client is failed"),
    CLIENT_NO_EXIST(506, "不存在应用"),
    PASSWORD_TOO_SHORT(507, "密码设定太短 (应大于8个字符长)"),
    PASSWORD_TOO_LONG(507, "密码设定太长 (应小于20个字符长)"),
    DIFFERENT_PASSWORD(508, "two inconsistent passwords"),
    REJECT_LOGIN(509, "reject login"),
    REJECT_REGISTER(510, "reject register"),
    UNSAFE_REQUEST(511, "不安全的请求"),
    ACCOUNT_REGISTERED(512, "account registered"),
    USER_ALREADY_EXIST(512, "用户已经存在"),
    EMAIL_REGISTERED(512, "email registered"),
    MOBILE_REGISTERED(512, "mobile registered"),
    USER_NOT_FOUND(513, "未找到用户"),

    CLIENT_IDENTITY_FAILED(514, "client identity failed"),
    CLIENT_IDENTITY_EMPTY(515, "client identity can`t empty"),
    CLIENT_IDENTITY_EXPIRED(516, "client identity is expired"),

    TOKEN_EXPIRE_TIME(517, "the token expire time"),
    TOKEN_CHECK_FAILED(518, "Token 验证失败"),

    SMS_VERIFY_CODE_FAILED(519, "短信验证码验证失败"),

    CAPTCHA_CODE_FAILED(520, "图片验证码验证失败"),

    MUST_SELECTED_MATERIAL(521, "Material to be produced must be selected"),

    CLIENT_NOT_FOUND(522, "can not found the client"),

    ACCOUNT_MUST_EMAIL(523, "account must is a email"),

    USER_REGISTER_FAILED(524, "用户注册异常"),

    VERIFY_SIGN_FAILED(525, "签名验证失败"),

    IP_RESTRICTED(526, "you IP restricted, try again later"),

    WAIT_A_MINUTE(527, "请等待一分钟"),
    TOKEN_CANNOT_REUSED(528, "Token cannot be reused"),
    MAIL_SEND_FAILED(529, "邮件发送失败"),
    EMAIL_OR_MOBILE(530, "请使用邮箱或手机号"),
    NICK_TOO_LONG(531, "昵称太长 (应小于20个字符长)"),
    OLD_PASSWORD_INCORRECT(532, "旧密码验证失败"),
    NEED_ONE_NAME(533, "需要设定名字"),
    CLIENT_NAME_EXIST(534, "客户端名称已经使用，请换个名字试试"),
    CLIENT_CANNOT_DELETE(535, "不能删除还有用户的Client"),
    NICK_NOT_EMPTY(536, "昵称不能为空"),

    OHO(999, "oho");


    private int code;

    private String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String message() {
        return this.message;
    }

    public int code() {
        return this.code;
    }
}
