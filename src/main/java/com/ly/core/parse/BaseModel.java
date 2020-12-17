package com.ly.core.parse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/3/31 17:38.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public abstract class BaseModel {
    protected Map<String, Object> headers;
    protected String name;
    protected String description;
    protected String url;
    protected String method;
    protected Map<String, List<Object>> validate;
    protected List<TestCase.HookInfo> setup;
    protected List<TestCase.HookInfo> teardown;
    protected List<TestCase.HookInfo> onFailure;
    protected List<Map<String, String>> saveGlobal;
    protected List<Map<String, String>> saveMethod;
    protected List<Map<String, String>> saveClass;
    protected List<Map<String, String>> saveThread;

    public final Object getRequests() {
        return data();
    }

    protected abstract Object data();
}
