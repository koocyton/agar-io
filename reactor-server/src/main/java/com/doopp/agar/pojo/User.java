package com.doopp.agar.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 5163L;

    private Long id;

    private String nickname;

    private int size;

    private int x;

    private int y;
}
