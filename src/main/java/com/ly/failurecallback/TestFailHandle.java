package com.ly.failurecallback;

import com.ly.core.base.BaseFailHandle;

/**
 * @Author: luoy
 * @Date: 2020/5/6 14:20.
 */
public class TestFailHandle implements BaseFailHandle<Object> {

    @Override
    public void handle(Object o) {
        System.out.println("this is onFailHandle");
    }
}
