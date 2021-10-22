package com.doopp.iogame.pojo;

public class Food extends Element {

    public Food(){
    }

    public Food(Integer id) {
        this.id = id;
        this.type = "food";
        this.grade = 1000;
        int color = (int)(Math.random() * 0x1000000);
        this.color = Integer.toHexString(color%256)
                + Integer.toHexString(color/256%256)
                + Integer.toHexString(color/256/256%256);
        this.x = (int)(Math.random() * 0x1500);
        this.y = (int)(Math.random() * 0x1500);
    }

    public String toString() {
        return "Food : " + this.grade
                + " id:" + this.id
                + " x:" + this.x
                + " y:" + this.y;
    }
}
