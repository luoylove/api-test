package com.ly.core.base;

/**
 * @Author: luoy
 * @Date: 2020/5/6 14:17
 */
@FunctionalInterface
public interface BaseProcessorHandle<T> {
    void processor(T t);
}
