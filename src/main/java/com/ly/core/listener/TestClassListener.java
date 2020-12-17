package com.ly.core.listener;

import com.ly.core.support.PostProcessorHolder;
import com.ly.core.support.TestNgClassPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.testng.IClassListener;
import org.testng.ITestClass;

/**
 * testNg Class生命周期 (涉及到一些并发和同步问题,不能和其他生命周期一起使用)
 * @Author: luoy
 * @Date: 2020/12/7 9:57.
 */
@Slf4j
public class TestClassListener implements IClassListener {
    @Override
    public void onBeforeClass(ITestClass testClass) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgClassPostProcessor.class)
                .forEach(testNgClassPostProcessor -> testNgClassPostProcessor.onTestClassStartBeforePostProcessor(testClass));
        log.info("=====================onBeforeClass:{} =====================", testClass.getName() );
    }

    @Override
    public void onAfterClass(ITestClass testClass) {
        PostProcessorHolder.getInstance().getPostProcessor(TestNgClassPostProcessor.class)
                .forEach(testNgClassPostProcessor -> testNgClassPostProcessor.onTestClassFinishAfterPostProcessor(testClass));
        log.info("=====================onAfterClass:{} =====================", testClass.getName() );
    }
}
