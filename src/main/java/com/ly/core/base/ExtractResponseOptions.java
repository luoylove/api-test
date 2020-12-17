package com.ly.core.base;

import com.ly.core.parse.BaseModel;

import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/5/6 16:01.
 */
public interface ExtractResponseOptions {
    /**
     * 获取返回头
     * @param header
     * @return
     */
    String getHeader(String header);

    /**
     * 获取返回值为json的body
     * @return
     */
    String getJsonBody();

    /**
     * 获取返回值为xml的body
     * @return
     */
    Object getXmlBody();

    /**
     * 获取返回值为html的body
     * @return
     */
    Object getHtmlBody();

    /**
     *获取所有返回头
     * @return
     */
    Map<String, String> getHeaders();

    /**
     * 获取返回状态code
     * @return
     */
    int getStatusCode();

    /**
     * 获取sessionId
     * @return
     */
    String getSessionId();

    /**
     * 获取cookie
     * @param name
     * @return
     */
    String getCookie(String name);

    /**
     * 获取所有cookie
     * @return
     */
    Map<String, String> cookies();

    /**
     * 获取path位置值
     * @param path
     * @return
     */
    Object getPath(String path);

    /**
     * 获取请求信息
     * @return
     */
    BaseModel getRequests();
}
