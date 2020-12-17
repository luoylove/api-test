package com.ly.core.matches;

import com.ly.core.utils.AssertUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @Author: luoy
 * @Date: 2020/3/26 16:56.
 */
public abstract class BaseSaveMatcher<T> extends BaseMatcher<T> {
    protected final String k;

    public BaseSaveMatcher(String k) {
        this.k = k;
    }

    @Override
    public boolean matches(Object v) {
        AssertUtils.notNull(v, "key:" + k + ",保存失败, 传入值为空");
        return save(v);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("key:" + k + ",保存失败");
    }

    protected abstract boolean save(Object v);
}
