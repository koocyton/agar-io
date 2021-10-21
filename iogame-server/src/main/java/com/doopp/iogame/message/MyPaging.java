package com.doopp.iogame.message;

import lombok.Data;

import java.util.List;

@Data
public class MyPaging<T> {

    private int code = 0;

    private String msg = "";

    private Long count = 0L;

    private List<T> data;

    private MyPaging() {
    }

    // 分页应该同 channel 绑定
    public static <D> MyPaging<D> ok(List<D> list) {
        // PageInfo<D> pageInfo = new PageInfo<>(list);
        MyPaging<D> pr = new MyPaging<D>();
        pr.data = list;
        // pr.count = pageInfo.getTotal();
        return pr;
    }
}
