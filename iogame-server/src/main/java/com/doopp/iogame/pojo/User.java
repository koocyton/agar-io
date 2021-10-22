package com.doopp.iogame.pojo;

public class User extends Element {

    public User(){
    }

    public String name;

    public long time;

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.type = "cell";
    }

    public void initData() {
        this.grade = 3999;
        this.type = "cell";
        this.x = (int) (Math.random()*0x1500);
        this.y = (int) (Math.random()*0x1500);
        this.time = System.currentTimeMillis();
        int _color = (int)(Math.random() * 0x1000000);
        this.color = Integer.toHexString(_color%256)
                + Integer.toHexString(_color/256%256)
                + Integer.toHexString(_color/256/256%256);
    }

    public String toString() {
        return this.type
                + " " + this.time
                + " " + this.id
                + " " + this.name
                + " " + this.color
                + " " + this.grade
                + " " + this.x
                + " " + this.y;
    }
}
