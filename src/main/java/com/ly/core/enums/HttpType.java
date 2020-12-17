package com.ly.core.enums;

/**
 * @Author: luoy
 * @Date: 2019/12/4 15:49.
 */
public enum HttpType {

    GET("get"),
    POST("post"),
    PUT("put"),
    DELETE("delete");

    private String type;

    HttpType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static HttpType get(String httpType) {
        switch (httpType) {
            case "get":
                return GET;
            case "post":
                return POST;
            case "put":
                return PUT;
            case "delete":
                return DELETE;
            default:
                    throw new IllegalArgumentException("No enum constant " + httpType);
        }
    }
}
