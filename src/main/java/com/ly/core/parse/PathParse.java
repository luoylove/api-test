package com.ly.core.parse;

/**
 * @Author: luoy
 * @Date: 2020/11/26 17:03.
 */
public interface PathParse {
    /**
     * 获取path值
     * @param path
     * @return
     */
    Object get(String path);

    /**
     * path是否存在
     * @param path
     * @return
     */
    boolean isExist(String path);

    int size(String path);
}
