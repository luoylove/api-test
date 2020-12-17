package com.ly.core.matches;

import com.ly.core.utils.ParameterizationUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 */
public abstract class BaseValidateMatcher<T> extends BaseMatcher<T> {
    protected  Map<String, List<Object>> validate;

    protected String errorText = "";

    public BaseValidateMatcher(Map<String, List<Object>> validate) {
        this.validate = validate;
    }

    @Override
    public boolean matches(Object operand) {
        validateParameterization();
        return validate(operand);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(errorText);
    }

    protected abstract boolean validate(Object v);

    /**
     * 断言参数化处理
     */
    private void validateParameterization() {
        ParameterizationUtil.wildcardMatcherValidate(validate);
    }
}
