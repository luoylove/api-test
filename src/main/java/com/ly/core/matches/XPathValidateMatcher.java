package com.ly.core.matches;

import com.ly.core.exception.BizException;
import com.ly.core.parse.PathParse;
import com.ly.core.parse.Xpath;

import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/11/27 17:01.
 */
public class XPathValidateMatcher extends PathValidateMatcherAdapter{
    public XPathValidateMatcher(Map validate) {
        super(validate);
    }

    @Override
    public PathParse getParseHandler(String operand) {
        try {
            Xpath xpath = Xpath.of(operand);
            return xpath;
        } catch (BizException e) {
            e.printStackTrace();
            this.errorText = "返回值xml解析错误: " + operand;
        }
        return null;
    }
}
