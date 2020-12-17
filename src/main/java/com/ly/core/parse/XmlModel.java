package com.ly.core.parse;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/4/16 17:51.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class XmlModel extends BaseModel {
    private String xmlData;

    @Builder
    public XmlModel(Map<String, Object> headers, String name, String description, String url, String method, Map<String, List<Object>> validate, List<TestCase.HookInfo> setup, List<TestCase.HookInfo> teardown, List<TestCase.HookInfo> onFailure, List<Map<String, String>> saveGlobal, List<Map<String, String>> saveMethod, List<Map<String, String>> saveClass, List<Map<String, String>> saveThread, String xmlData) {
        super(headers, name, description, url, method, validate, setup, teardown, onFailure, saveGlobal, saveMethod, saveClass, saveThread);
        this.xmlData = xmlData;
    }

    @Override
    protected Object data() {
        return xmlData;
    }
}
