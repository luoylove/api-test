package com.ly.core.matches;

import com.ly.core.utils.ContextDataStorage;

/**
 * @Author: luoy
 * @Date: 2020/3/26 16:56.
 */
public class SaveGlobalMatcher<T> extends BaseSaveMatcher<T> {
    public SaveGlobalMatcher(String k) {
        super(k);
    }

    @Override
    public boolean save(Object v) {
        ContextDataStorage.getInstance().setAttribute(k, v);
        return true;
    }
}
