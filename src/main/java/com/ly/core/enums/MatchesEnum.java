package com.ly.core.enums;

/**
 * @Author: luoy
 */
public enum MatchesEnum {

    /**相等*/
    EQ("eq"),
    /**为空*/
    NULL("isNull"),
    /**不为空*/
    NOTNULL("notNull"),
    /**调用PluginSuppot类方法支持**/
    PLUGIN("plugin"),
    /**大于*/
    GT("gt"),
    /**小于*/
    LT("lt"),
    /**包含*/
    CONTAINS("contains"),
    /** map key匹配*/
    HASKEY("hasKey"),
    /** map value匹配*/
    HASVAULE("hasValve"),
    LEN("len");


    private String type;

    MatchesEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
