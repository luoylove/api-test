package com.ly.core.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: luoy
 * @Date: 2019/12/4 15:49.
 */
public enum ModelType {
    JSON("json"),
    XML("xml"),
    TEXT("text"),
    FORM("form");

    private String type;

    ModelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ModelType get(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        for (ModelType modelType : ModelType.values()) {
            if (modelType.getType().equals(type)) {
                return modelType;
            }
        }
        return null;
    }
}
