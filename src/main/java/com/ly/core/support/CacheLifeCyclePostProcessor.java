package com.ly.core.support;

import com.ly.core.utils.ContextDataStorage;
import org.springframework.stereotype.Component;
import org.testng.ITestContext;
import org.testng.ITestResult;

/**
 * @Author: luoy
 * @Date: 2020/12/2 17:39.
 */
@Component
public class CacheLifeCyclePostProcessor extends TestNgLifeCyclePostProcessorAdapter implements HttpPostProcessor{

    @Override
    public void onTestMethodSuccessAfterPostProcessor(ITestResult result){
        //method缓存清理
        ContextDataStorage.getInstance().removeAllMethodAttribute();
    }

    @Override
    public void onTestMethodFailureAfterPostProcessor(ITestResult result){
        //method缓存清理
        ContextDataStorage.getInstance().removeAllMethodAttribute();
    }

    @Override
    public void onAllTestMethodFinishAfterPostProcessor(ITestContext context){
        //线程缓存清理
        ContextDataStorage.getInstance().removeAllThreadAttribute();
    }

    @Override
    public void requestsBeforePostProcessor(HttpContext context) {

    }

    @Override
    public void responseAfterPostProcessor(HttpContext context) {

    }

    @Override
    public void responseDonePostProcessor(HttpContext context) {
        //处理请求级别缓存清除
        ContextDataStorage.getInstance().removeAllAskAttribute();
    }
}
