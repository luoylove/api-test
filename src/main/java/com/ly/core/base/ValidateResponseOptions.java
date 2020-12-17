package com.ly.core.base;

import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/5/6 15:56.
        */
public interface ValidateResponseOptions<R> {
    /**
     * path方式断言, 如果返回content-type 不是application/json与application/xml 无法使用该断言
     * @param path
     * @param matcher
     *          org.hamcrest.Matche
     * @return
     */
    R validate(String path, org.hamcrest.Matcher matcher);

    /**
     * yaml方式断言
     * @param validate
     * @return
     */
    R validate(Map<String, List<Object>> validate);

    /**
     * 比较值是否与expected相等
     * @param actual
     * @param expected
     * @return
     */
    R eq(Object actual, Object expected);

    /**
     * 比较Path是否与expected相等
     * path方式断言, 如果返回content-type 不是application/json与application/xml 无法使用该断言
     * @param path
     * @param expected
     * @return
     */
    R eqByPath(String path, Object expected);

    /**
     * 调用方法的方式验证
     * @param method
     * @param args
     * @return
     */
    R validatePlugin(String method, Object...args);
}
