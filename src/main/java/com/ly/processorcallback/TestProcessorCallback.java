package com.ly.processorcallback;

import com.ly.core.base.BaseProcessorHandle;

/**
 * test
 * @Author: luoy
 * @Date: 2020/5/7 13:18.
 */
public class TestProcessorCallback implements BaseProcessorHandle<String> {
    @Override
    public void processor(String o) {
        System.out.println("this is test Processor");
    }
}
