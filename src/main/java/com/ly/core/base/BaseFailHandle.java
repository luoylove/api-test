package com.ly.core.base;

/**
 * @Author: luoy
 * @Date: 2020/5/6 14:17
 */
@FunctionalInterface
public interface BaseFailHandle<T> {
    void handle(T t);
}
