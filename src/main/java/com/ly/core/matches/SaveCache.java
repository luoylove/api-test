package com.ly.core.matches;

/**
 * @Author: luoy
 * @Date: 2020/3/26 17:19.
 */
public class SaveCache {

    /**
     *  保存值到缓存,作用域为thread
     * @param k
     * @param <T>
     * @return
     */
    public static <T> org.hamcrest.Matcher<T> saveSuite(String k) {
        return new SaveThreadMatcher<>(k);
    }

    /**
     * 保存值到缓存,作用域method
     * @param k
     * @param <T>
     * @return
     */
    public static <T> org.hamcrest.Matcher<T> saveTest(String k) {
        return new SaveMethodMatcher<>(k);
    }

    /**
     * 保存值到缓存,作用域全局
     * @param k
     * @param <T>
     * @return
     */
    public static <T> org.hamcrest.Matcher<T> saveGlobal(String k) {
        return new SaveGlobalMatcher<>(k);
    }
}
