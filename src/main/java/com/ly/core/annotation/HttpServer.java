package com.ly.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: luoy
 * @Date: 2019/8/21 17:17.
 *
 * 接口类标注,必填,否则无法扫描到容器,无法注入
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
public @interface HttpServer {
    /**
     * 非必填<br>
     * 支持${}通配符,只需要在配置文件spring.httprul下配置即可<br>
     * 优先取接口上url,如果接口url为带http/https则最终请求url为接口url， 如果不带http/https，则最终请求url为baseUrl + 接口url
     * @return
     */
    String baseUrl() default "";
}
