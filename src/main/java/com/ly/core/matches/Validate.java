package com.ly.core.matches;

import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 */
public class Validate {
    /**
     * yaml jsonPath 断言
     * @param validate
     * @param <T>
     * @return
     */
    public static <T> org.hamcrest.Matcher<T> validateJson(Map<String, List<Object>> validate) {
        return new JsonPathValidateMatcher(validate);
    }

    /**
     * yaml xpath断言
     * @param validate
     * @param <T>
     * @return
     */
    public static <T> org.hamcrest.Matcher<T> validateXpath(Map<String, List<Object>> validate) {
        return new XPathValidateMatcher(validate);
    }

    public static <T> org.hamcrest.Matcher<T> validateEq(Object actual, Object expected) {
        return new EqValidateMatcher<>(actual, expected);
    }
}
