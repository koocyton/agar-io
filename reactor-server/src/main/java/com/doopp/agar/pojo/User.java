package com.doopp.agar.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 5163L;

    private Long id;

    private String name;

    private String color;

    private String type;

    private int grade;

    private double x;

    private double y;

    private long time;

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
