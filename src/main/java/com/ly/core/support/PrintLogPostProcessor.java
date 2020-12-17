package com.ly.core.support;

import com.ly.core.listener.LifeCycleListenerProcessorUtil;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;
import org.testng.ITestResult;

/**
 * @Author: luoy
 * @Date: 2020/12/2 17:39.
 */
@Component
public class PrintLogPostProcessor extends TestNgLifeCyclePostProcessorAdapter {
    @Override
    public void onAllTestMethodFinishAfterPostProcessor(ITestContext context){
        LifeCycleListenerProcessorUtil.create().printLog(context);
    }

    @Override
    public void onConfigurationStartBeforePostProcessor(ITestResult tr){
        LifeCycleListenerProcessorUtil.create().skipConfigurationStartPrintLog(tr);
    }

}
