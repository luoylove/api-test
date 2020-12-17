package com.ly.core.matches;

import com.ly.core.enums.MatchesEnum;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @Author: luoy
 */
public class EqValidateMatcher<T> extends BaseMatcher<T> {
    private Object actual;

    private Object expected;

    protected String errorText = "";

    public EqValidateMatcher(Object actual, Object expected) {
        this.actual = actual;
        this.expected = expected;
    }

    @Override
    public boolean matches(Object operand) {
        boolean isReturn = ValidateUtils.eq(actual, expected);
        if (!isReturn) {
            this.errorText = ValidateByPathHandlers.prettyErrorMsg(expected, actual, null, MatchesEnum.EQ, "两值不匹配");
        }
        return isReturn;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(errorText);
    }
}
