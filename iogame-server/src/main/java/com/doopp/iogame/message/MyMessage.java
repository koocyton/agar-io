package com.doopp.iogame.message;

public class MyMessage<T> {

    private String roomName;

    private String action;

    private int userCount;

    private T data;

    // 没有他 jackson 就不解析，蛋疼不
    public MyMessage() {
    }

    public static <Z> MyMessage<Z> ok(String action, String roomName, int userCountC, Z data) {
        MyMessage<Z> wooMessage =  new MyMessage<Z>();
        wooMessage.setRoomName(roomName);
        wooMessage.setAction(action);
        wooMessage.setData(data);
        wooMessage.setUserCount(userCountC);
        return wooMessage;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return this.roomName;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
