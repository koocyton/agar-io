package com.doopp.agar.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Food implements Serializable {

    private static final long serialVersionUID = 5163L;

    private String color;

    private String type;

    private int grade;

    private int x;

    private int y;

    public String toString() {
        return this.type
                + " " + this.color
                + " " + this.grade
                + " " + this.x
                + " " + this.y;
    }
}
