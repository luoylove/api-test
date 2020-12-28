package com.ly.core.parse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/4/17 14:53.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    private String name;
    private String description;
    private String type;
    private String url;
    private String method;
    private List<HookInfo> setup;
    private List<HookInfo> teardown;
    private List<HookInfo> onFailure;
    private List<Map<String, String>> saveGlobal;
    private List<Map<String, String>> saveMethod;
    private List<Map<String, String>> saveClass;
    private List<Map<String, String>> saveThread;
    private Map<String, Object> headers;
    private Map<String, Object> requests;
    /** 请求为[]格式,只用于支持请求为json */
    private List<Object> requestsList;
    private Map<String, List<Object>> validate;
    private List<Parameters> parameters;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameters {
        private String name;
        private String description;
        private Map<String, Object> headers;
        private Map<String, Object> requests;
        /** 请求为[]格式 */
        private List<Object> requestsList;
        private Map<String, List<Object>> validate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HookInfo {
        private String method;
        private String args;
    }
}
