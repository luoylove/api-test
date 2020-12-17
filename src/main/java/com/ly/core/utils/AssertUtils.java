package com.ly.core.utils;

import com.ly.core.exception.BizException;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2019/8/1 17:24.
 */
public class AssertUtils {
    private static BizException createBizException(String message) {
        if (StringUtils.isNotBlank(message)) {
            return new BizException(message);
        } else {
            return new BizException();
        }
    }

    private static void assertIfNecessary(boolean condition, String message) {
        if (!condition) {
            throw createBizException(message);
        }
    }

    public static <T> void isNull(T obj) {
        isNull(obj, null);
    }

    public static <T> void isNull(T obj, String message) {
        assertIfNecessary(obj == null, message);
    }

    public static <T> void isNull(T obj, String message, Object... args) {
        isNull(obj, String.format(message, args));
    }

    public static <T> void notNull(T obj) {
        notNull(obj, null);
    }

    public static <T> void notNull(T obj, String message) {
        assertIfNecessary(obj != null, message);
    }

    public static <T> void notNull(T obj, String message, Object... args) {
        notNull(obj, String.format(message, args));
    }

    public static void isEmpty(Collection<?> collection, String message) {
        assertIfNecessary(collection == null || collection.isEmpty(), message);
    }

    public static void isEmpty(Collection<?> collection) {
        isEmpty(collection, null);
    }

    public static void isEmpty(Collection<?> collection, String message, Object...args) {
        isEmpty(collection, String.format(message, args));
    }

    public static void isEmpty(Map<?, ?> map) {
        isEmpty(map, null);
    }

    public static void isEmpty(Map<?, ?> map, String message) {
        assertIfNecessary(map == null || map.isEmpty(), message);
    }

    public static void isEmpty(Map<?, ?> map, String message, Object...args) {
        isEmpty(map, String.format(message, args));
    }

    public static void isNotEmpty(Map<?, ?> map, String message) {
        assertIfNecessary(map != null && !map.isEmpty(), message);
    }

    public static void isNotEmpty(Map<?, ?> map) {
        isNotEmpty(map, null);
    }

    public static void isNotEmpty(Map<?, ?> map, String message, Object...args) {
        isNotEmpty(map, String.format(message, args));
    }

    public static void isNotEmpty(Collection<?> collection, String message) {
        assertIfNecessary(collection != null && !collection.isEmpty(), message);
    }

    public static void isNotEmpty(Collection<?> collection, String message, Object...args) {
        isNotEmpty(collection, String.format(message, args));
    }

    public static void isNotEmpty(Collection<?> collection) {
        isNotEmpty(collection, null);
    }

    public static void isFalse(Boolean flag, String message) {
        assertIfNecessary(!flag, message);
    }

    public static void isFalse(Boolean flag) {
        isFalse(flag, null);
    }

    public static void isFalse(Boolean flag, String message, Object...args) {
        isFalse(flag, String.format(message, args));
    }

    public static void isTrue(Boolean flag, String message) {
        assertIfNecessary(flag, message);
    }

    public static void isTrue(Boolean flag) {
        isTrue(flag, null);
    }

    public static void isTrue(Boolean flag, String message, Object...args) {
        isTrue(flag, String.format(message, args));
    }

    public static <T> void eq(T t1, T t2, String message) {
        assertIfNecessary(t1.equals(t2), message);
    }

    public static <T> void eq(T t1, T t2) {
        eq(t1.equals(t2), null);
    }

    public static <T> void eq(T t1, T t2, String message, Object...args) {
        eq(t1.equals(t2), String.format(message, args));
    }

    public static void isBlank(String s, String message) {
        assertIfNecessary(StringUtils.isBlank(s), message);
    }

    public static void isBlank(String s) {
        isBlank(s, null);
    }

    public static void isBlank(String s, String message, Object...args) {
        isBlank(s, String.format(message, args));
    }

    public static void eqInt(int i1, int i2, String message) {
        assertIfNecessary(i1 == i2, message);
    }

    public static void eqInt(int i1, int i2) {
        eqInt(i1, i2, null);
    }

    public static void eqInt(int i1, int i2, String message, Object...args) {
        eqInt(i1, i2, String.format(message, args));
    }

    /**
     * i1是否大于i2,小于或者等于抛异常
     *
     * @param i1
     * @param i2
     * @param message
     */
    public static void gt(int i1, int i2, String message) {
        assertIfNecessary(i1 > i2, message);
    }

    public static void gt(int i1, int i2) {
        gt(i1, i2, null);
    }

    public static void gt(int i1, int i2, String message, Object...args) {
        gt(i1, i2, String.format(message, args));
    }


    /**
     * i1是否小于i2,大于或者等于抛异常
     *
     * @param i1
     * @param i2
     * @param message
     */
    public static void lt(int i1, int i2, String message) {
        assertIfNecessary(i1 < i2, message);
    }

    public static void lt(int i1, int i2) {
        lt(i1, i2, null);
    }

    public static void lt(int i1, int i2, String message, Object...args) {
        lt(i1, i2, String.format(message, args));
    }

    /**
     * 表达式成立则抛异常
     *
     * @param exp
     * @param message
     */
    public static void expression(boolean exp, String message) {
        assertIfNecessary(!exp, message);
    }

    public static void expression(boolean exp) {
        expression(exp, null);
    }

    public static void expression(boolean exp, String message, Object...args) {
        expression(exp, String.format(message, args));
    }
}
