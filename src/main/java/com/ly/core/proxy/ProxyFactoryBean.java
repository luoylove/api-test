package com.ly.core.proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @Author: luoy
 * @Date: 2019/9/9 16:08.
 *  InitializingBean：初始化时afterPropertiesSet被调用，生成clazz类型的代理类对象
 */
public class ProxyFactoryBean implements FactoryBean<Object>, InitializingBean {
    private Class<?> clazz;

    private Object proxy;


    @Override
    public Object getObject() throws Exception {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return clazz;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.proxy = ProxyApi.getProxyInstance(clazz);
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }
}
