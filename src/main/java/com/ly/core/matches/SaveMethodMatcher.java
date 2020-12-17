package com.ly.core.matches;

import com.ly.core.utils.ContextDataStorage;

/**
 * @Author: luoy
 * @Date: 2020/3/26 16:56.
 */
public class SaveMethodMatcher<T> extends BaseSaveMatcher<T> {

    public SaveMethodMatcher(String k) {
        super(k);
    }

    @Override
    public boolean save(Object v) {
        ContextDataStorage.getInstance().setMethodAttribute(k, v);
        return true;
    }
}
