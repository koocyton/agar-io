package com.doopp.iogame.pojo;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 5163L;

    public Integer id;

    public String name;

    public String color;

    public String type;

    public int grade;

    public double x;

    public double y;

    public long time;

    public User() {

    }

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.type = "cell";
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
