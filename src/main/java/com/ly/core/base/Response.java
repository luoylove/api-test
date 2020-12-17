package com.ly.core.base;

import com.ly.core.config.DefaultConstantConfig;
import com.ly.core.enums.MatchesEnum;
import com.ly.core.exception.AssertionException;
import com.ly.core.matches.Validate;
import com.ly.core.matches.ValidateUtils;
import com.ly.core.parse.BaseModel;
import com.ly.core.support.HttpContext;
import com.ly.core.support.HttpPostProcessor;
import com.ly.core.support.PostProcessorHolder;
import com.ly.core.utils.AssertUtils;
import com.ly.core.utils.ContextDataStorage;
import com.ly.core.utils.JSONSerializerUtil;
import com.ly.core.utils.PatternUtil;
import com.ly.core.utils.ReflectUtil;
import com.ly.core.utils.Utils;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lombok.extern.slf4j.Slf4j;
import org.testng.collections.Maps;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: luoy
 * @Date: 2020/5/6 13:40.
 */
@Slf4j
public class Response implements ValidateResponseOptions<Response>, SaveDataResponseOptions<Response>, ExtractResponseOptions,
                    FailureResponseOptions<Response>, ProcessorResponseOptions<Response>, AutoParseResponseOptions<Response>{
    private io.restassured.response.Response assuredResponse;

    private RuntimeException ex;

    private BaseModel model;

    private ContentType contentType;

    private HttpContext context;

    private Response(){}

    /** Syntactic sugar */
    public Response then() {
        return this;
    }

    public Response(io.restassured.response.Response assuredResponse, HttpContext context) {
        this.assuredResponse = assuredResponse;
        this.context = context;
        this.context.setResponse(this);
        this.model = context.getBaseModel();
        this.contentType = ContentType.fromContentType(assuredResponse.getContentType());
    }

    public Response statusCode(int code) {
        if (exitsEx())  {
            return this;
        }
        try {
            assuredResponse.then().statusCode(code);
        } catch (Exception ex) {
            this.ex = new AssertionException(ex.getMessage(), ex.getCause());
        }
        return this;
    }

    @Override
    public Response validate(String path, org.hamcrest.Matcher matcher) {
        if (exitsEx())  {
            return this;
        }
        try {
            assuredResponse.then().body(path, matcher);
        } catch (Exception ex) {
            this.ex = new AssertionException(ex.getMessage(), ex.getCause());
        }
        return this;
    }

    @Override
    public Response validate(Map<String, List<Object>> validate) {
        if (exitsEx())  {
            return this;
        }
        try {
            if (contentType == ContentType.XML) {
                assuredResponse.then().body(Validate.validateXpath(validate));
            }
            else if (contentType == ContentType.JSON) {
                assuredResponse.then().body(Validate.validateJson(validate));
            }
            //default
            else {
                assuredResponse.then().body(Validate.validateJson(validate));
            }
        } catch (Throwable ex) {
            this.ex = new AssertionException(ex.getMessage(), ex.getCause());
        }
        return this;
    }

    @Override
    public Response eq(Object actual, Object expected) {
        if (exitsEx())  {
            return this;
        }
        try {
            assuredResponse.then().body(Validate.validateEq(actual, expected));
        } catch (Throwable ex) {
            this.ex = new AssertionException(ex.getMessage(), ex.getCause());
        }
        return this;
    }

    @Override
    public Response validatePlugin(String method, Object...args) {
        Map<String, List<Object>> validate = new HashMap<>(1);
        List<Object> data = new ArrayList<>(1);
        Map<String, Object> map = new HashMap<>(1);
        map.put(ValidateUtils.METHOD_NAME_KEY, method);
        map.put(ValidateUtils.METHOD_ARGS_KEY, args);
        data.add(map);
        validate.put(MatchesEnum.PLUGIN.getType(), data);
        validate(validate);
        return this;
    }

    @Override
    public Response eqByPath(String path, Object expected) {
        Map<String, List<Object>> validate = new HashMap<>(1);
        List<Object> data = new ArrayList<>(1);
        Map<String, Object> map = new HashMap<>(1);
        map.put(path, expected);
        data.add(map);
        validate.put(MatchesEnum.EQ.getType(), data);
        validate(validate);
        return this;
    }

    @Override
    public Response saveGlobal(String key, String path) {
        if (exitsEx())  {
            return this;
        }
        ContextDataStorage.getInstance().setAttribute(key, getPathValue(path));
        return this;
    }

    @Override
    public Response saveMethod(String key, String path) {
        if (exitsEx())  {
            return this;
        }
        ContextDataStorage.getInstance().setMethodAttribute(key, getPathValue(path));
        return this;
    }

    @Override
    public Response saveClass(String key, String path) {
        if (exitsEx())  {
            return this;
        }
        ContextDataStorage.getInstance().setClassAttribute(key, getPathValue(path));
        return this;
    }

    @Override
    public Response saveThread(String key, String path) {
        if (exitsEx())  {
            return this;
        }
        ContextDataStorage.getInstance().setThreadAttribute(key, getPathValue(path));
        return this;
    }

    @Override
    public Response onFailure(BaseFailHandle failHandle) {
        if (ex != null) {
            log.info("======response.onFailure处理开始========");
            try {
                failHandle.handle(null);
            } catch (Exception e) {
                log.error("Response.onFailure: " , e);
                e.printStackTrace();
            }
            log.info("======response.onFailure处理结束========");
        }
        return this;
    }

    @Override
    public <T> Response onFailure(T t, BaseFailHandle<T> failHandle) {
        if (ex != null) {
            log.info("======response.onFailure处理开始========");
            try {
                failHandle.handle(t);
            } catch (Exception e) {
                log.error("Response.onFailure: " , e);
                e.printStackTrace();
            }
            log.info("======response.onFailure处理结束========");
        }
        return this;
    }

    @Override
    public Response onFailureByExpr(String expressions, BaseFailHandle<String> failHandle) {
        if (ex != null) {
            log.info("======response.onFailure处理开始========");
            try {
                String replace = PatternUtil.replace(expressions);
                if (replace.equals(expressions)) {
                    failHandle.handle(getPathValueOrDefault(expressions));
                } else {
                    failHandle.handle(replace);
                }
            } catch (Exception e) {
                log.error("Response.onFailure: " , e);
                e.printStackTrace();
            }
            log.info("======response.onFailure处理结束========");
        }
        return this;
    }

    @Override
    public Response onFailure(Class<? extends BaseFailHandle<Object>> clazz, Object...args) {
        try {
            if (args == null || args.length == 0) {
                onFailure(clazz.newInstance());
            } else {
                Constructor<? extends BaseFailHandle<Object>> declaredConstructor = clazz.getDeclaredConstructor(Utils.getParamsType(args));
                onFailure(declaredConstructor.newInstance(args));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public <T> Response onFailure(T t, Class<? extends BaseFailHandle<T>> clazz, Object...args) {
        try {
            if (args == null || args.length == 0) {
                onFailure(t, clazz.newInstance());
            } else {
                Constructor<? extends BaseFailHandle<T>> declaredConstructor = clazz.getDeclaredConstructor(Utils.getParamsType(args));
                onFailure(t, declaredConstructor.newInstance(args));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Response onFailureByExpr(String expressions, Class<? extends BaseFailHandle<String>> clazz, Object...args) {
        try {
            if (args == null || args.length == 0) {
                onFailureByExpr(expressions, clazz.newInstance());
            } else {
                Constructor<? extends BaseFailHandle<String>> declaredConstructor = clazz.getDeclaredConstructor(Utils.getParamsType(args));
                onFailureByExpr(expressions, declaredConstructor.newInstance(args));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    /**
     * 结束并且设置Allure状态, 处理yml tearDown
     */
    public void done() {
        context.setThrowable(ex);
        PostProcessorHolder.getInstance().getPostProcessor(HttpPostProcessor.class)
                .forEach(httpPostProcessor -> httpPostProcessor.responseDonePostProcessor(context));
        if (ex != null) {
            throw ex;
        }
    }

    /**
     * 取值
     * @return
     */
    public ExtractResponseOptions extract() {
        done();
        return this;
    }

    @Override
    public String getHeader(String header) {
        return assuredResponse.getHeader(header);
    }

    @Override
    public String getJsonBody() {
        Object o = assuredResponse.jsonPath().get();
        return JSONSerializerUtil.serialize(o);
    }

    @Override
    public Object getXmlBody() {
        return assuredResponse.xmlPath().get();
    }

    @Override
    public Object getHtmlBody() {
        return assuredResponse.xmlPath().get();
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = Maps.newHashMap();
        Iterator<Header> iterator = assuredResponse.getHeaders().iterator();
        while (iterator.hasNext()) {
            Header next = iterator.next();
            headers.put(next.getName(), next.getValue());
        }
        return headers;
    }

    @Override
    public int getStatusCode() {
        return assuredResponse.getStatusCode();
    }

    @Override
    public String getSessionId() {
        return assuredResponse.getSessionId();
    }

    @Override
    public String getCookie(String name) {
        return assuredResponse.getCookie(name);
    }

    @Override
    public Map<String, String> cookies() {
        return assuredResponse.getCookies();
    }

    @Override
    public Object getPath(String path) {
        return assuredResponse.path(path);
    }

    @Override
    public BaseModel getRequests() {
        return this.model;
    }

    @Override
    public Response processor(BaseProcessorHandle<Object> processorHandle) {
        log.info("======response.processor处理开始========");
        try {
            processorHandle.processor(null);
        } catch (Exception e) {
            log.error("Response.processor: " , e);
            e.printStackTrace();
        }
        log.info("======response.processor处理结束========");

        return this;
    }

    @Override
    public <T> Response processor(T t, BaseProcessorHandle<T> processorHandle) {
        log.info("======response.processor处理开始========");
        try {
            processorHandle.processor(t);
        } catch (Exception e) {
            log.error("Response.processor: " , e);
            e.printStackTrace();
        }
        log.info("======response.processor处理结束========");
        return this;
    }

    @Override
    public Response processorByExpr(String expressions, BaseProcessorHandle<String> processorHandle) {
        log.info("======response.processor处理========");
        try {
            String replace = PatternUtil.replace(expressions);
            if (replace.equals(expressions)) {
                processorHandle.processor(getPathValueOrDefault(expressions));
            } else {
                processorHandle.processor(replace);
            }
        } catch (Exception e) {
            log.error("Response.processor: ", e);
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Response processor(Class<? extends BaseProcessorHandle<Object>> processorHandle, Object...args) {
        try {
            if (args == null || args.length == 0) {
                processor(processorHandle.newInstance());
            } else {
                Constructor<? extends BaseProcessorHandle<Object>> declaredConstructor = processorHandle.getDeclaredConstructor(Utils.getParamsType(args));
                processor(declaredConstructor.newInstance(args));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public <T> Response processor(T t, Class<? extends BaseProcessorHandle<T>> processorHandle, Object...args) {
        try {
            if (args == null || args.length == 0) {
                processor(t, processorHandle.newInstance());
            } else {
                Constructor<? extends BaseProcessorHandle<T>> declaredConstructor = processorHandle.getDeclaredConstructor(Utils.getParamsType(args));
                processor(t, declaredConstructor.newInstance(args));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Response processorByExpr(String expressions, Class<? extends BaseProcessorHandle<String>> processorHandle, Object...args) {
        try {
            if (args == null || args.length == 0) {
                processorByExpr(expressions, processorHandle.newInstance());
            } else {
                Constructor<? extends BaseProcessorHandle<String>> declaredConstructor = processorHandle.getDeclaredConstructor(Utils.getParamsType(args));
                processorByExpr(expressions, declaredConstructor.newInstance(args));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    private String getPathValueOrDefault(String path) {
        if (contentType == ContentType.XML) {
            return assuredResponse.xmlPath().get(path);
        }
        return assuredResponse.jsonPath().get(path);
    }

    private String getPathValue(String path) {
        if (contentType == ContentType.XML) {
            String v = assuredResponse.xmlPath().get(path);
            AssertUtils.notNull(v, "path值不存在:" + path);
            return v;
        }
        //default
        String v = assuredResponse.jsonPath().get(path);
        AssertUtils.notNull(v, "path值不存在:" + path);
        return v;
    }

    private boolean exitsEx() {
        return ex != null ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public Response waitMs(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Response wait(TimeUnit unit, long time) {
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public Response auto(int httpStatusCode) {
        autoExcludeDone(httpStatusCode).done();
        return this;
    }

    @Override
    public Response auto() {
        return auto(200);
    }

    @Override
    public Response autoExcludeDone() {
        return autoExcludeDone(200);
    }

    @Override
    public Response autoExcludeDone(int httpStatusCode) {
        then();
        //http status
        statusCode(httpStatusCode);
        //validate
        validate(model.getValidate());
        //save
        if(model.getSaveGlobal() != null && model.getSaveGlobal().size() > 0) {
            model.getSaveGlobal().forEach( data -> data.forEach(this::saveGlobal));
        }
        if(model.getSaveThread() != null && model.getSaveThread().size() > 0) {
            model.getSaveThread().forEach( data -> data.forEach(this::saveThread));
        }
        if(model.getSaveClass() != null && model.getSaveClass().size() > 0) {
            model.getSaveClass().forEach( data -> data.forEach(this::saveClass));
        }
        if(model.getSaveMethod() != null && model.getSaveMethod().size() > 0) {
            model.getSaveMethod().forEach( data -> data.forEach(this::saveMethod));
        }

        //onFailure
        if(model.getOnFailure() != null && model.getOnFailure().size() > 0) {
            onFailure(obj -> model.getOnFailure().forEach( hook -> ReflectUtil.reflectInvoke(DefaultConstantConfig.ONFAILURE_METHOD_BY_CLASS, hook.getMethod(), hook.getArgs())));
        }
        return this;
    }
}
