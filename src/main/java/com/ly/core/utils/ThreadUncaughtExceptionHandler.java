package com.ly.core.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: luoy
 * @Date: 2019/12/18 15:17.
 */
@Slf4j
public class ThreadUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("线程:" + t.getName() + "异常", e);
    }
}
