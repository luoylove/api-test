package com.ly.core.proxy;


import com.google.common.collect.Lists;
import com.ly.core.annotation.Filters;
import com.ly.core.annotation.HttpServer;
import com.ly.core.base.BaseTestCase;
import com.ly.core.enums.HttpType;
import com.ly.core.exception.BizException;
import com.ly.core.parse.BaseModel;
import com.ly.core.parse.JsonModel;
import com.ly.core.parse.MultipleModel;
import com.ly.core.parse.XmlModel;
import com.ly.core.restassured.RestassuredHttpHandleBuilder;
import com.ly.core.support.HttpContext;
import com.ly.core.support.HttpPostProcessor;
import com.ly.core.support.PostProcessorHolder;
import com.ly.core.utils.AssertUtils;
import com.ly.core.utils.ContextDataStorage;
import com.ly.core.utils.ParameterizationUtil;
import com.ly.core.utils.PatternUtil;
import com.ly.core.utils.ResourcesUtil;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.collections.Maps;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/3/19 15:08.
 */
public class ProxySupport {

    private final static String BASE_HTTP_PREFIX = "spring.httpurl.";

    private RestassuredHttpHandleBuilder httpClient = RestassuredHttpHandleBuilder.create();

    private List<Filter> filters = Lists.newArrayList();
    private Map<String, Object> params4Form = Maps.newHashMap();
    private String params4String = "";
    private List<BaseModel> baseModels;
    private Class clazz;

    private Method method;

    public ProxySupport(Class clazz, Method method, Object[] args) {
        this.clazz = clazz;
        this.method = method;
        argsToBaseModel(args);
    }

    public final com.ly.core.base.Response service() {
        baseUrlProcessor();
        filtersProcessor();
        return methodProcessor();
    }

    /**
     * 入参类型转换并且检查
     */
    private void argsToBaseModel(Object[] args) {
        this.baseModels = Lists.newArrayListWithCapacity(args.length);
        for (int i = 0; i < args.length; i++) {
            BaseModel baseModel = null;
            if (args[i] instanceof BaseModel) {
                baseModel = (BaseModel) args[i];
            } else if (args[i] instanceof String) {
                Object params = ContextDataStorage.getInstance().getMethodAttribute(BaseTestCase.PARAMETER_KEY);
                if (params instanceof BaseModel) {
                    baseModel = (BaseModel) params;
                }
                if (params instanceof MultipleModel) {
                    baseModel = ((MultipleModel) params).getModel(String.valueOf(args[i]));
                }
            } else {
                throw new BizException("类型转换错误,需要类型: BaseModel.class, 实际类型:" + args[i].getClass().getName());
            }
            check(baseModel);
            baseModels.add(baseModel);
        }
    }


    /**
     * baseUrl
     */
    private void baseUrlProcessor() {
        HttpServer httpServerAnnotation = (HttpServer) clazz.getAnnotation(HttpServer.class);
        if (!StringUtils.isBlank(httpServerAnnotation.baseUrl())) {
            String url = httpServerAnnotation.baseUrl();
            List<String> patterns = PatternUtil.getPatterns(url);
            if (patterns.size() > 0) {
                for (String p : patterns) {
                    String v = ResourcesUtil.getProp(new StringBuffer(BASE_HTTP_PREFIX).append(p).toString());
                    AssertUtils.notNull(v, PatternUtil.createKey(p) + "配置文件中未找到配置");
                    url = url.replace(PatternUtil.createKey(p), v);
                }
            }
            httpClient.setBaseUrl(url);
        }
    }

    /**
     * filters
     */
    private void filtersProcessor() {
        Filters classFilters = (Filters) clazz.getAnnotation(Filters.class);

        Filters methodFilters = method.getAnnotation(Filters.class);

        doFilter(classFilters);
        doFilter(methodFilters);
    }

    /**
     * 参数化
     */
    private void parameterizationProcessor() {
        baseModels.forEach(baseModel -> {
            ParameterizationUtil.wildcardMatcherHeadersAndRequests(baseModel.getHeaders());
            if (baseModel.getRequests() instanceof Map) {
                params4Form = (Map<String, Object>) baseModel.getRequests();
                ParameterizationUtil.wildcardMatcherHeadersAndRequests(params4Form);
            }

            if (baseModel.getRequests() instanceof String) {
                params4String = ParameterizationUtil.wildcardMatcherString((String) baseModel.getRequests());
            }
        });
    }

    /**
     * method
     *
     * @return
     */
    private com.ly.core.base.Response methodProcessor() {
        for (BaseModel baseModel : baseModels) {
            HttpContext.HttpContextBuilder builder = HttpContext.builder();
            builder.baseModel(baseModel);
            PostProcessorHolder.getInstance().getPostProcessor(HttpPostProcessor.class)
                    .forEach(httpPostProcessor -> httpPostProcessor.requestsBeforePostProcessor(builder.build()));
            parameterizationProcessor();
            com.ly.core.base.Response response = new com.ly.core.base.Response(invokeHttpMethod(baseModel), builder.build());
            PostProcessorHolder.getInstance().getPostProcessor(HttpPostProcessor.class)
                    .forEach(httpPostProcessor -> httpPostProcessor.responseAfterPostProcessor(builder.response(response).build()));
            return response;
        }
        return null;
    }

    private Response invokeHttpMethod(BaseModel baseModel) {
        switch (HttpType.get(baseModel.getMethod().toLowerCase())) {
            case GET:
                return doGet(baseModel);
            case PUT:
                return doPut(baseModel);
            case POST:
                return doPost(baseModel);
            case DELETE:
                return doDelete(baseModel);
            default:
                throw new BizException("不支持的方法:" + baseModel.getMethod());
        }
    }

    private void doFilter(Filters filter) {
        if (filter == null) {
            return;
        }
        for (int i = 0; i < filter.value().length; i++) {
            try {
                filters.add(filter.value()[i].newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Response doGet(BaseModel model) {
        return httpClient.get(model.getHeaders(), params4Form, model.getUrl(), filters.toArray(new Filter[filters.size()]));
    }

    private Response doPost(BaseModel model) {
        if (StringUtils.isNotBlank(params4String)) {
            if (model instanceof XmlModel) {
                return httpClient.post(model.getHeaders(), params4String, ContentType.XML, model.getUrl(), filters.toArray(new Filter[filters.size()]));
            }

            if (model instanceof JsonModel) {
                return httpClient.post(model.getHeaders(), params4String, ContentType.JSON, model.getUrl(), filters.toArray(new Filter[filters.size()]));
            }
            throw new BizException("暂不支持model: " + model.getClass());
        }

        return httpClient.post(model.getHeaders(), params4Form, model.getUrl(), filters.toArray(new Filter[filters.size()]));
    }

    private Response doPut(BaseModel model) {
        if (StringUtils.isNotBlank(params4String)) {
            if (model instanceof XmlModel) {
                return httpClient.put(model.getHeaders(), params4String, ContentType.XML, model.getUrl(), filters.toArray(new Filter[filters.size()]));
            }

            if (model instanceof JsonModel) {
                return httpClient.put(model.getHeaders(), params4String, ContentType.JSON, model.getUrl(), filters.toArray(new Filter[filters.size()]));
            }
            throw new BizException("暂不支持model: " + model.getClass());
        }

        return httpClient.put(model.getHeaders(), params4Form, model.getUrl(), filters.toArray(new Filter[filters.size()]));
    }

    private Response doDelete(BaseModel model) {
        return httpClient.delete(model.getHeaders(), params4Form, model.getUrl(), filters.toArray(new Filter[filters.size()]));
    }

    private void check(BaseModel model) {
        AssertUtils.notNull(model, "http请求model为null");
//        AssertUtils.notNull(model.getName(), "http请求model name为null");
        AssertUtils.notNull(model.getUrl(), "http请求url为null, model:" + model.getName());
        AssertUtils.notNull(model.getMethod(), "http请求method为null, model:" + model.getName());
    }
}