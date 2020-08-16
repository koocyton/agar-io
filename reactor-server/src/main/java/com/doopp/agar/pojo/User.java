package com.doopp.agar.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 5163L;

    private Long id;

    private String name;

    private String color;

    private String action;

    private int grade;

    private int x;

    private int y;

    public String toString() {
        return this.id
                + " " + this.name
                + " " + this.color
                + " " + this.action
                + " " + this.grade
                + " " + this.x
                + " " + this.y;
    }
}
