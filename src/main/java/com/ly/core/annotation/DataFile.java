package com.ly.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by luoy on 2019/6/17.
 * <p>数据驱动设置数据读取方式与数据路径
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface DataFile {
    Format format() default Format.CSV;

    String path();

    enum Format
    {
        CSV,
        EXCEL,
        XML,
        YML,
        JSON
    }
}
