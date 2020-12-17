package com.ly.core.support;

import com.ly.core.exception.BizException;
import com.ly.core.parse.BaseModel;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * @Author: luoy
 * @Date: 2020/12/1 19:58.
 */
@Component
public class AllureSetStepHttpPostProcessor extends TestNgLifeCyclePostProcessorAdapter implements HttpPostProcessor{
    private static final InheritableThreadLocal<AllureLifecycle> LIFECYCLE
            = new InheritableThreadLocal<AllureLifecycle>() {
        @Override
        protected AllureLifecycle initialValue() {
            return Allure.getLifecycle();
        }
    };

    @Override
    public void requestsBeforePostProcessor(HttpContext context) {
        BaseModel baseModel = context.getBaseModel();
        if(baseModel == null) {
            return;
        }
        doSetStartStep(getUrlPath(baseModel.getUrl()), baseModel.getDescription());
    }

    @Override
    public void responseAfterPostProcessor(HttpContext context) {

    }

    @Override
    public void responseDonePostProcessor(HttpContext context) {
        if(!existStep()) {
            return;
        }
        Throwable ex = context.getThrowable();
        if (ex != null) {
            LIFECYCLE.get().updateStep(s -> s.setStatus(Status.BROKEN));
        } else  {
            LIFECYCLE.get().updateStep(s -> s.setStatus(Status.PASSED));
        }
        LIFECYCLE.get().stopStep();
    }

    /**
     * 给每个接口设置allure @step注解
     */
    private void doSetStartStep(String url, String description) {
        if(!existStep()) {
            return;
        }
        final String uuid = UUID.randomUUID().toString();
        String stepTitle = "[%s] [%s]";
        final StepResult result = new StepResult()
                .setName(String.format(stepTitle, url, description != null ? description : ""));
        LIFECYCLE.get().startStep(uuid, result);
    }

    private String getUrlPath(String url) {
        if (!url.contains("http")) {
            return url;
        }
        try {
            URL baseUrl = new URL(url);
            return baseUrl.getPath();
        } catch (MalformedURLException e) {
            throw new BizException("url解析失败", e);
        }
    }

    private boolean existStep() {
        return Allure.getLifecycle().getCurrentTestCaseOrStep().isPresent();
    }
}
