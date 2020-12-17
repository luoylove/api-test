package com.ly.core.parse;

import com.ly.core.utils.AssertUtils;
import lombok.Data;

import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/4/17 16:56.
 */
@Data
public class MultipleModel<T extends BaseModel> {
    private Map<String, T> data;

    public T getModel(String name) {
        T v = data.get(name);
        AssertUtils.notNull(v, "model不存在: " + name);
        return v;
    }
}
