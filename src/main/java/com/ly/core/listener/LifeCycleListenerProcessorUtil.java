package com.ly.core.listener;

import com.google.common.collect.Lists;
import com.ly.core.annotation.TestSetup;
import com.ly.core.notification.HandlerNotificationContext;
import com.ly.core.utils.ResourcesUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LifeCycleListener通用处理器
 *
 * @Author: luoy
 * @Date: 2019/9/5 15:38.
 */
@Slf4j
public class LifeCycleListenerProcessorUtil {

    private static volatile LifeCycleListenerProcessorUtil INSTANCE;

    private LifeCycleListenerProcessorUtil() {
    }

    public static LifeCycleListenerProcessorUtil create() {
        if (INSTANCE == null) {
            synchronized (LifeCycleListenerProcessorUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LifeCycleListenerProcessorUtil();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 通知处理器
     */
    public LifeCycleListenerProcessorUtil notificationOnFinish() {
        String channel = ResourcesUtil.getProp("notification.channel");

        HandlerNotificationContext handlerContext = new HandlerNotificationContext();

        if (HandlerNotificationContext.Channel.No.getType().equals(channel)) {
            log.info("不进行通知");
        } else if (HandlerNotificationContext.Channel.ALL.getType().equals(channel)) {
            log.info("全渠道通知");
            handlerContext.getInstanceAll().forEach(h -> h.notification());
        } else {
            log.info("单渠道通知:{}", channel);
            handlerContext.getInstance(channel);
        }
        return this;
    }

    public LifeCycleListenerProcessorUtil testSetup(ITestContext context) {
        ITestNGMethod[] allTestMethods = context.getAllTestMethods();
        Set<Class> clazzSet = new HashSet<>();
        for (ITestNGMethod method : allTestMethods) {
            clazzSet.add(method.getRealClass());
        }

        clazzSet.forEach(clazz -> {
            TestSetup testSetup = (TestSetup) clazz.getAnnotation(TestSetup.class);
            if (testSetup != null) {
//                hookProcessor(testSetup.value());
            }
        });

        return this;
    }


    /**
     * 打印错误日志
     * @param context
     * @return
     */
    public LifeCycleListenerProcessorUtil printLog(ITestContext context) {
        context.getFailedTests().getAllResults().stream().forEach(e -> log.error("Failed method:" +  e.getName(), e.getThrowable()));

        context.getFailedConfigurations().getAllResults().stream().forEach(e -> log.error("FailedConfigurations method:" +  e.getName(), e.getThrowable()));

        context.getSkippedConfigurations().getAllResults().stream().forEach(e -> log.error("SkippedConfigurations method:" +  e.getName(), e.getThrowable()));

        context.getSkippedTests().getAllResults().stream().forEach(e -> log.error("Skipped method:" +  e.getName(), e.getThrowable()));
        return this;
    }

    /**
     * 过滤掉因为继承AbstractTestNGSpringContextTests类而执行的Configuration方法日志打印
     * @param tr
     * @return
     */
    public LifeCycleListenerProcessorUtil skipConfigurationStartPrintLog(ITestResult tr) {
        List<String> skipNames = Lists.newArrayList("springTestContextPrepareTestInstance", "springTestContextBeforeTestClass", "springTestContextBeforeTestMethod"
                                                    , "springTestContextAfterTestMethod", "springTestContextAfterTestClass");
        if (!skipNames.contains(tr.getName())) {
            log.info("=====================onConfigurationStart: {}=====================", tr.getName());
        }
        return this;
    }

}
