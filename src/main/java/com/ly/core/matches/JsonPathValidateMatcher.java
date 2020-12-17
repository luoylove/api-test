package com.ly.core.matches;

import com.ly.core.parse.JsonPath;
import com.ly.core.parse.PathParse;
import com.ly.core.utils.Utils;

import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/11/27 17:01.
 */
public class JsonPathValidateMatcher extends PathValidateMatcherAdapter{
    public JsonPathValidateMatcher(Map validate) {
        super(validate);
    }

    @Override
    public PathParse getParseHandler(String operand) {
        if (!Utils.isJSONValid(operand)) {
            this.errorText = "返回值不为json:" + operand.getClass();
            return null;
        }
        return JsonPath.create(operand);
    }
}
