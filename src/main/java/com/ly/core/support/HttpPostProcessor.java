package com.ly.core.support;

import com.ly.core.base.Response;

/**
 * http请求后置处理器
 * @Author: luoy
 * @Date: 2020/12/1 18:08.
 */
public interface HttpPostProcessor extends PostProcessor{

    /**
     * http请求之前处理器
     * @param context
     */
    void requestsBeforePostProcessor(HttpContext context);

    /**
     * http请求之后处理器
     * @param context
     */
    void responseAfterPostProcessor(HttpContext context);


    /**
     * http请求后 对response对象进行各种处理后的处理器
     * 在{@link Response done()}内调用
     * @param context
     */
    void responseDonePostProcessor(HttpContext context);
}
