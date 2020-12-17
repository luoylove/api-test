package com.ly.core.proxy;

import java.lang.reflect.Proxy;

/**
 * 接口动态代理类
 * @Author: luoy
 * @Date: 2019/8/21 17:03.
 */
public class ProxyApi {
    public static <T> T getProxyInstance(Class<T> clazz) {

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                (proxy, method, args) -> {
                    if(method.getName().equals(ProxyMethodName.doHttp.name())) {
                        ProxySupport execute = new ProxySupport(clazz, method, args);
                        return execute.service();
                    } else {
                        ProxySupportBefore before = new ProxySupportBefore(clazz, method, args);
                        return before.handle();
                    }
                });
    }

    public enum ProxyMethodName{
        doHttp
    }
}
