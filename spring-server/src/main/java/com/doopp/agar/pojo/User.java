package com.doopp.agar.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private Long id;

    private String nickname;

    private int size;

    private int x;

    private int y;

    private int mouseX;

    private int mouseY;
}
