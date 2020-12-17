package com.ly.core.support;

import com.ly.core.listener.LifeCycleListenerProcessorUtil;
import org.springframework.stereotype.Component;
import org.testng.ISuite;

/**
 * @Author: luoy
 * @Date: 2020/12/2 17:39.
 */
@Component
public class NotificationPostProcessor extends TestNgLifeCyclePostProcessorAdapter{
    @Override
    public void onSuiteFinishAfterPostProcessor(ISuite suite){
        LifeCycleListenerProcessorUtil.create().notificationOnFinish();
    }
}
