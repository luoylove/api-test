package com.ly.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * <p>设置testCase重试次数, default=3
 * <p>可在类上直接加该注解,作用域整个类
 * <p>可在方法上加该注解，作用域该方法
 * <p>如果两种都加了,优先取方法上count
 * <p>如果ide内直接运行类该注解需要起作用需要设置@Test(retryAnalyzer = RetryAndlyzer.class)
 * <p>如果在testng.xml中运行，@Test注解中不需要再增加retryAnalyzer，需要增加过滤器：
 * {@code
 * <listeners>
 *         <listener class-name="com.ly.core.listener.BaseLifeCycleListener" />
 * </listeners>
 * }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, TYPE})
public @interface RetryCount {
    int count() default 3;
}
