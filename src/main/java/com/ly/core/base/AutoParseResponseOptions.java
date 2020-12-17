package com.ly.core.base;

import java.util.concurrent.TimeUnit;

/**
 * @Author: luoy
 * @Date: 2020/5/6 15:58.
 */
public interface AutoParseResponseOptions<R> {

    /**
     * 等待毫秒数
     * @param ms
     * @return
     */
    R waitMs(long ms);

    /**
     *等待时间
     * @param unit
     * @param time
     * @return
     */
    R wait(TimeUnit unit, long time);

    /**
     * 自动解析yml文件, 默认返回值为200
     * @return
     */
    R auto();

    /**
     * 自动解析yml文件, 返回值为httpStatusCode
     * @param httpStatusCode
     * @return
     */
    R auto(int httpStatusCode);

    /**
     * 自动解析yml文件, 但是不会自动调用done()方法结束,需要手动调用done结束
     * 主要用于给该http请求添加更多的自定义处理
     * eg. autoExcludeDone().saveThread("a", "a").done();
     * @return
     */
    R autoExcludeDone();

    /**
     * 手动设置httpStatusCode,处理与autoExcludeDone()一致
     * @param httpStatusCode
     * @return
     */
    R autoExcludeDone(int httpStatusCode);
}
