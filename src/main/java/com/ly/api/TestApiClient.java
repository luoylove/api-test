package com.ly.api;

import com.ly.core.annotation.Filters;
import com.ly.core.annotation.HttpServer;
import com.ly.headersfilter.RestAssuredLogFilter;
import com.ly.headersfilter.TestHeadersFilter;

/**
 * @Author: luoy
 * @Date: 2020/5/14 11:05.
 */
@HttpServer(baseUrl = "${test.url}")
@Filters({RestAssuredLogFilter.class, TestHeadersFilter.class})
public interface TestApiClient extends BaseHttpClient{
}
