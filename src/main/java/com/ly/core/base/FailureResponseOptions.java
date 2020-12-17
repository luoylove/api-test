package com.ly.core.base;

/**
 * @Author: luoy
 * @Date: 2020/5/6 15:58.
 */
public interface FailureResponseOptions<R> {
    /**
     * 自定义无入参处理 实现BaseFailHandle类并且重写handle方法或者lambda表达式编写
     * @param failHandle
     * @return
     */
    R onFailure(BaseFailHandle<Object> failHandle);

    /**
     * 自定义有入参处理
     * @param t
     *          传入参数
     * @param failHandle
     *          失败处理逻辑
     * @return
     */
    <T> R onFailure(T t, BaseFailHandle<T> failHandle);

    /**
     * 支持通配符${} 与Path
     * @param expressions
     * @param failHandle
     * @return
     */
    R onFailureByExpr(String expressions, BaseFailHandle<String> failHandle);

    /**
     * 自定义无入参处理 clazz类型为实现BaseFailHandle类
     * @param clazz
     * @param args
     *        有参构造函数入参
     * @return
     */
    R onFailure(Class<? extends BaseFailHandle<Object>> clazz, Object...args);

    /**
     * 自定义有入参处理
     * @param t
     *          传入参数
     * @param clazz
     *          失败处理逻辑 类型为实现BaseFailHandle类
     * @param args
     *        有参构造函数入参
     * @return
     */
    <T> R onFailure(T t, Class<? extends BaseFailHandle<T>> clazz, Object...args);

    /**
     * 支持通配符${} 与path
     * @param expressions
     * @param clazz 类型为实现BaseFailHandle类
     * @param args
     *        有参构造函数入参
     * @return
     */
    R onFailureByExpr(String expressions, Class<? extends BaseFailHandle<String>> clazz, Object...args);
}
