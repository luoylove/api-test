package com.ly.core.enums;

/**
 * @Author: luoy
 * @Date: 2019/12/4 15:49.
 */
public enum ModelType {
    JSON("json"),
    XML("xml"),
    FORM("form");

    private String type;

    ModelType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
