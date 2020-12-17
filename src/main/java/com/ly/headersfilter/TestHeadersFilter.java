package com.ly.headersfilter;

import com.ly.core.listener.HeadersFilterAdapter;
import com.ly.core.utils.MapTool;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2019/8/16 17:11.
 */
@Slf4j
public class TestHeadersFilter extends HeadersFilterAdapter {
    @Override
    public Map<String, String> defHeaders() {
        return MapTool.builder()
                .put("Content-Type", "application/json;charset=utf-8")
                .build();
    }
}
