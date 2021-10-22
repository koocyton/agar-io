package com.doopp.iogame.pojo;

import java.io.Serializable;

abstract public class Element implements Serializable  {

    private static final long serialVersionUID = 51263L;

    public Integer id;

    public double x;

    public double y;

    public String color;

    public String type;

    public int grade;
}
