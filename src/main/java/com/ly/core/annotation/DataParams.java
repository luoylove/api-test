package com.ly.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: luoy
 * @Date: 2019/8/2 10:52.
 *
 * <p>数据驱动直接设置数据
 *  数据格式：
 *  单次运行数据：{"13000000001,000001"}
 *  多次运行数据：{"13000000001,000001","13000000002,000002"}
 *  数据个数与测试方法入参个数一致
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface DataParams {

    String[] value() default {};

    String splitBy() default ",";
}
