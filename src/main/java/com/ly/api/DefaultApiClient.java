package com.ly.api;

import com.ly.core.annotation.Filters;
import com.ly.core.annotation.HttpServer;
import com.ly.headersfilter.RestAssuredLogFilter;

/**
 * 该类做通用默认调用,只添加一个日志过滤器
 * @Author: luoy
 * @Date: 2020/5/14 11:05.
 */
@HttpServer
@Filters({RestAssuredLogFilter.class})
public interface DefaultApiClient extends BaseHttpClient{
}
