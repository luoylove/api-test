package com.ly.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ==> AfterMethod
 * @Author: luoy
 * 测试结束操作
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ApiAfterMethod {
    /**
     * 排除的方法
     */
    String[] excludeOnMethod() default {};
}
