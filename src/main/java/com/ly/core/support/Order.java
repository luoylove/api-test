package com.ly.core.support;

/**
 * 排序接口
 * @Author: luoy
 * @Date: 2020/12/2 17:50.
 */
public interface Order {
    /**
     * 数字越小优先级越高
     * @return
     */
    int getOrder();
}
