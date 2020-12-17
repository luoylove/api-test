package com.ly.core.matches;

import com.ly.core.utils.ContextDataStorage;

/**
 * @Author: luoy
 * @Date: 2020/3/26 16:56.
 */
public class SaveThreadMatcher<T> extends BaseSaveMatcher<T> {
    public SaveThreadMatcher(String k) {
        super(k);
    }

    @Override
    public boolean save(Object v) {
        ContextDataStorage.getInstance().setThreadAttribute(k, v);
        return true;
    }
}
