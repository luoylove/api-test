package com.ly.headersfilter;

import com.ly.core.utils.JSONSerializerUtil;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2019/8/16 17:11.
 */
@Slf4j
public class RestAssuredLogFilter implements Filter {
    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);

        String url = requestSpec.getURI();
        String method = requestSpec.getMethod();
        Headers headers = requestSpec.getHeaders();

        Map reqFormParams =  requestSpec.getFormParams();
        String requestsStr = reqFormParams != null && reqFormParams.size() > 0 ? JSONSerializerUtil.serialize(reqFormParams)
                        : JSONSerializerUtil.serialize(requestSpec.getBody());
        String responseStr = response.asString();
        log.info("url: [{}]", url);
        log.info("method: [{}]", method);
        log.info("requestHeaders: {}", headers.asList());
        log.info("request: [{}]", requestsStr);
        log.info("response: [{}]", responseStr);
        log.info("===========================done============================");
        return response;
    }
}
