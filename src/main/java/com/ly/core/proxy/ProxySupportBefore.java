package com.ly.core.proxy;


import com.ly.api.BaseHttpClient;
import com.ly.core.base.Response;
import com.ly.core.parse.BaseModel;
import com.ly.core.utils.ContextDataStorage;
import com.ly.core.utils.SpringContextUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @Author: luoy
 * @Date: 2020/3/19 15:08.
 */
public class ProxySupportBefore<T extends BaseHttpClient> implements BaseHttpClient{

    private Class<T> clazz;

    private Method method;

    private Object[] args;

    public ProxySupportBefore(Class<T> clazz, Method method, Object[] args) {
        this.clazz = clazz;
        this.method = method;
        this.args = args;
    }

    public final T handle() {
        try {
            method.invoke(this, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return SpringContextUtil.getBean(clazz);
    }

    @Override
    public BaseHttpClient wait(TimeUnit unit, long interval) {
        try {
            unit.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public BaseHttpClient saveAsk(String k, Object v) {
        ContextDataStorage.getInstance().setAskAttribute(k, v);
        return this;
    }

    @Override
    public BaseHttpClient saveMethod(String k, Object v) {
        ContextDataStorage.getInstance().setMethodAttribute(k, v);
        return this;
    }

    @Override
    public BaseHttpClient saveThread(String k, Object v) {
        ContextDataStorage.getInstance().setThreadAttribute(k, v);
        return this;
    }

    @Override
    public BaseHttpClient saveGlobal(String k, Object v) {
        ContextDataStorage.getInstance().setAttribute(k, v);
        return this;
    }

    @Override
    public Response doHttp(BaseModel model) {
        return null;
    }

    @Override
    public Response doHttp(String modelName) {
        return null;
    }
}