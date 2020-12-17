package com.ly.core.base;

/**
 * @Author: luoy
 * @Date: 2020/5/6 15:58.
 */
public interface ProcessorResponseOptions<R> {
    /**
     * 自定义无入参处理 实现BaseProcessorHandle类并且重写processor方法或者lambda表达式编写
     * @param processorHandle
     * @return
     */
    R processor(BaseProcessorHandle<Object> processorHandle);

    /**
     * 自定义有入参处理
     * @param t
     *          需要传入的参数
     * @param processorHandle
     *          处理逻辑
     * @return
     */
    <T> R processor(T t, BaseProcessorHandle<T> processorHandle);

    /**
     * 支持通配符${} 与path表达式
     * @param expressions
     * @param processorHandle
     * @return
     */
    R processorByExpr(String expressions, BaseProcessorHandle<String> processorHandle);

    /**
     * 自定义无入参处理 processorHandle extends BaseProcessorHandle
     * @param processorHandle
     * @param args
     *        有参构造函数入参
     * @return
     */
    R processor(Class<? extends BaseProcessorHandle<Object>> processorHandle, Object...args);

    /**
     * 自定义有入参处理 processorHandle extends BaseProcessorHandle
     * @param object
     *          需要传入的参数
     * @param processorHandle
     *          处理逻辑
     * @param args
     *        有参构造函数入参
     * @return
     */
    <T> R processor(T object, Class<? extends BaseProcessorHandle<T>> processorHandle, Object...args);

    /**
     * 支持通配符${} 与Path表达式    processorHandle extends BaseProcessorHandle
     * @param expressions
     * @param processorHandle
     * @param args
     *        有参构造函数入参
     * @return
     */
    R processorByExpr(String expressions, Class<? extends BaseProcessorHandle<String>> processorHandle, Object...args);
}
