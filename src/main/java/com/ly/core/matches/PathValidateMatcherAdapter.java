package com.ly.core.matches;

import com.ly.core.exception.BizException;
import com.ly.core.parse.PathParse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * path 断言适配器
 * @Author: luoy
 */
public abstract class PathValidateMatcherAdapter<T> extends BaseValidateMatcher<T> {
    public PathValidateMatcherAdapter(Map<String, List<Object>> validate) {
        super(validate);
    }

    @Override
    protected boolean validate(Object operand) {

        if (!(operand instanceof String)) {
            this.errorText = "返回值类型匹配出错,需要类型: String,实际类型: " + operand.getClass();
            return false;
        }

        if (validate == null || validate.size() == 0) {
            return true;
        }

        PathParse pathParse = getParseHandler(String.valueOf(operand));

        if (pathParse == null) {
            return false;
        }

        ValidateByPathHandlers validateByJsonHandlers = new ValidateByPathHandlers(pathParse);
        Method[] declaredMethods = validateByJsonHandlers.getClass().getDeclaredMethods();
        boolean isMethodExist = false;

        for(String k: validate.keySet()) {
            for(Method method : declaredMethods) {
                if (k.equals(method.getName())) {
                    isMethodExist = true;
                    for(Object obj : validate.get(k)) {
                        try {
                            method.invoke(validateByJsonHandlers, obj);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            if(e.getTargetException() instanceof BizException) {
                                this.errorText = e.getTargetException().getMessage();
                                return false;
                            }
                            throw new BizException("%s类, 方法:%s, 异常: %s", validateByJsonHandlers.getClass().getName(), k, e.getTargetException());
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                            throw new BizException("yaml validate解析错误,请检查: " + validate.get(k));
                        }
                    }
                }
            }
            if (!isMethodExist) {
                throw new BizException("断言方法不存在: " + k);
            }
        }
        return true;
    }

    /** 获取path解析器*/
    public abstract PathParse getParseHandler(String operand);
}
