package com.ly.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/11/9 15:56.
 */
public class AnnotationUtils {

    /**
     * 通过反射的方式修改注解字段的值
     * @param instance  注解实例
     * @param propertyKey 注解属性名
     * @param propertyValue 修改后的值
     */
    public static void updateAnnotationValue(Annotation instance, String propertyKey, Object propertyValue) {
        if (instance == null) {
            return;
        }
        try{
            //获取@test注解这个代理实例所持有的 InvocationHandler
            InvocationHandler h = Proxy.getInvocationHandler(instance);
            // 获取 AnnotationInvocationHandler 的 memberValues 字段
            Field hField = h.getClass().getDeclaredField("memberValues");
            // 因为这个字段事 private final 修饰，所以要打开权限
            hField.setAccessible(true);
            // 获取 memberValues
            Map memberValues = (Map) hField.get(h);
            // 修改 value 属性值
            memberValues.put(propertyKey, propertyValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
