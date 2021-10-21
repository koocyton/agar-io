package com.doopp.iogame.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Food implements Serializable {

    private static final long serialVersionUID = 5163L;

    private Integer id;

    private Integer x;

    private Integer y;

    private String color;

    private String type;

    private int grade;

    public String toString() {
        return this.type
                + " " + this.id
                + " " + this.color
                + " " + this.grade
                + " " + this.x
                + " " + this.y;
    }
}
