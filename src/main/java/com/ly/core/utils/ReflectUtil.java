package com.ly.core.utils;

import com.ly.core.exception.BizException;
import com.ly.core.matches.ValidateUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/9/28 10:35.
 */
public class ReflectUtil {
    public static <T> Object reflectInvoke(Class<T> clazz, String methodName, String argsString) {
        String[] args = StringUtils.isBlank(argsString) ? new String[0] : argsString.split(",");
        return reflectInvoke(clazz, methodName, args);
    }

    public static <T> Object reflectInvoke(Class<T> clazz, String methodName, Object[] args) {
        try {
            if (StringUtils.isBlank(methodName)) {
                return null;
            }

            T obj = SpringContextUtil.getBean(clazz) != null ? SpringContextUtil.getBean(clazz) : clazz.newInstance();
            Method[] declaredMethods = obj.getClass().getDeclaredMethods();
            boolean isMethodExist = false;
            for(Method method : declaredMethods) {
                if (methodName.equals(method.getName())) {
                    isMethodExist = true;
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    AssertUtils.eqInt(args.length, parameterTypes.length, "%s,该方法入参不匹配", methodName);
                    Object[] invokeArgs = new Object[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (args[i] instanceof String) {
                            String arg = ParameterizationUtil.wildcardMatcherString((String) args[i]);
                            arg = arg.trim();
                            String simpleName = parameterTypes[i].getSimpleName();
                            if ("String".equals(simpleName)) {
                                invokeArgs[i] = String.valueOf(arg);
                            } else if ("Integer".equals(simpleName) || "int".equals(simpleName)) {
                                invokeArgs[i] = Integer.valueOf(arg);
                            } else if ("Boolean".equals(simpleName) || "boolean".equals(simpleName)) {
                                invokeArgs[i] = Boolean.valueOf(arg);
                            } else if ("Float".equals(simpleName) || "float".equals(simpleName)) {
                                invokeArgs[i] = Float.valueOf(arg);
                            } else if ("Double".equals(simpleName) || "double".equals(simpleName)) {
                                invokeArgs[i] = Double.valueOf(arg);
                            } else {
                                throw new BizException("%s类,方法: %s,入参类型: %s,不支持该类型", clazz.getName(), methodName, simpleName);
                            }
                        } else {
                            invokeArgs[i] = args[i];
                        }
                    }
                    try {
                        return method.invoke(obj, invokeArgs);
                    } catch (InvocationTargetException e) {
                        if (e.getTargetException() instanceof BizException){
                            throw (BizException) e.getTargetException();
                        }
                        throw new BizException("%s类, 方法: %s,异常: %s", clazz.getName(), methodName, e.getTargetException());
                    } catch (IllegalArgumentException e) {
                        throw new BizException("%s类, 方法: %s,参数不匹配", clazz.getName(), methodName);
                    }
                }
            }
            if (!isMethodExist) {
                throw new BizException("%s类, 方法: %s未找到", clazz.getName(), methodName);
            }
        } catch (InstantiationException e) {
            throw new BizException("%s, 实例化异常,该类是一个抽象类或接口", clazz.getName());
        } catch (IllegalAccessException e) {
            throw new BizException("%s,非法访问异常,请设置setAccessible(true)", clazz.getName());
        }
        return null;
    }

    public static <T> Object[] hooksCall(String[] hooks, Class<T> clazz) {
        if (hooks == null || hooks.length <= 0) {
            return new Object[0];
        }
        Object[] res = new Object[hooks.length];
        for (int i = 0; i < hooks.length; i++) {
            Map<String, String> methodAndArgs = PatternUtil.extractHookByRegular(hooks[i]);
            String methodName = methodAndArgs.get(ValidateUtils.METHOD_NAME_KEY);
            String args = methodAndArgs.containsKey(ValidateUtils.METHOD_ARGS_KEY) ? methodAndArgs.get(ValidateUtils.METHOD_ARGS_KEY) : null;
            Object invokeRes = ReflectUtil.reflectInvoke(clazz, methodName, args);
            res[i] = invokeRes;
        }
        return res;
    }
}
