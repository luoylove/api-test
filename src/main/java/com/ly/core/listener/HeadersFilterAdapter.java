package com.ly.core.listener;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 一个设置http请求头部的适配器
 * @Author: luoy
 * @Date: 2019/8/16 17:11.
 */
@Slf4j
public abstract class HeadersFilterAdapter implements Filter {
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Map<String, String> defHeaders = defHeaders();

        defHeaders.forEach((k ,v) -> {
            if(StringUtils.isBlank(requestSpec.getHeaders().getValue(k.toLowerCase())) && !StringUtils.isBlank(v)) {
                requestSpec.header(k.toLowerCase(), v);
            }
        });
        return ctx.next(requestSpec, responseSpec);
    }

    public abstract Map<String, String> defHeaders();
}
