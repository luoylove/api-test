package com.ly.core.support;

import com.ly.core.config.DefaultConstantConfig;
import com.ly.core.parse.BaseModel;
import com.ly.core.parse.TestCase;
import com.ly.core.utils.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @Author: luoy
 * @Date: 2020/12/1 19:58.
 */
@Component
public class HookHttpPostProcessor implements HttpPostProcessor {
    @Override
    public void requestsBeforePostProcessor(HttpContext context) {
        BaseModel model = context.getBaseModel();
        if(model == null) {
            return;
        }

        getSetupHook(context).forEach(hook -> {
            //此处处理${request}参数化, 把${request} => baseModel, yam文件其他地方请勿用${request} ${response}引用
            String[] args = StringUtils.isBlank(hook.getArgs()) ? new String[0] : hook.getArgs().split(",");
            Object[] obj = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if ("${request}".equals(args[i])) {
                    obj[i] = model;
                } else {
                    obj[i] = args[i];
                }
            }
            ReflectUtil.reflectInvoke(DefaultConstantConfig.SETUP_METHOD_BY_CLASS, hook.getMethod(), obj);
        });
    }

    @Override
    public void responseAfterPostProcessor(HttpContext context) {
    }

    @Override
    public void responseDonePostProcessor(HttpContext context) {
        BaseModel model = context.getBaseModel();
        if(model == null) {
            return;
        }
        getTeardownHook(context).forEach( hook -> {
            //此处处理${response}参数化, 把${response} => com.ly.core.base.Response, yam文件其他地方请勿用${request} ${response}引用
            String[] args = StringUtils.isBlank(hook.getArgs()) ? new String[0] : hook.getArgs().split(",");
            Object[] obj = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                if ("${response}".equals(args[i])) {
                    obj[i] = context.getResponse();
                } else {
                    obj[i] = args[i];
                }
            }
            ReflectUtil.reflectInvoke(DefaultConstantConfig.TEARDOWN_METHOD_BY_CLASS, hook.getMethod(), obj);
        });
    }

    private List<TestCase.HookInfo> getSetupHook(HttpContext context) {
        List<TestCase.HookInfo> hooks = context.getBaseModel().getSetup();
        if (hooks == null || hooks.size() <= 0) {
            return Collections.emptyList();
        }
        return hooks;
    }

    private List<TestCase.HookInfo> getTeardownHook(HttpContext context) {
        List<TestCase.HookInfo> hooks = context.getBaseModel().getTeardown();
        if (hooks == null || hooks.size() <= 0) {
            return Collections.emptyList();
        }
        return hooks;
    }
}
