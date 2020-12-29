package com.ly.core.utils;

import com.ly.core.exception.BizException;
import com.ly.core.parse.FormModel;
import com.ly.core.parse.JsonModel;
import com.ly.core.parse.MapToXml;
import com.ly.core.parse.TestCase;
import com.ly.core.parse.TextModel;
import com.ly.core.parse.XmlModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 深拷贝
 *
 * @Author: luoy
 * @Date: 2020/5/14 17:55.
 */
public class TestCase2BaseModel {

    public static JsonModel toJsonModel(TestCase testCase) {
        if (Objects.nonNull(testCase.getRequests())) {
            if (!(testCase.getRequests() instanceof Map || testCase.getRequests() instanceof List)) {
                throw new BizException("Requests格式错误,当type为json时必须为键值对或者数组, 错误modelName:%s", testCase.getName());
            }
        }
        return JSONSerializerUtil.copy(JsonModel.builder()
                .headers(testCase.getHeaders())
                .name(testCase.getName())
                .url(testCase.getUrl())
                .description(testCase.getDescription())
                .method(testCase.getMethod())
                .jsonData(JSONSerializerUtil.serialize(testCase.getRequests()))
                .validate(testCase.getValidate())
                .setup(testCase.getSetup())
                .saveMethod(testCase.getSaveMethod())
                .saveClass(testCase.getSaveClass())
                .saveGlobal(testCase.getSaveGlobal())
                .saveThread(testCase.getSaveThread())
                .onFailure(testCase.getOnFailure())
                .teardown(testCase.getTeardown())
                .build(), JsonModel.class);
    }

    public static XmlModel toXmlModel(TestCase testCase) {
        if (Objects.nonNull(testCase.getRequests())) {
            if (!(testCase.getRequests() instanceof Map)) {
                throw new BizException("Requests格式错误,当type为xml时必须为键值对, 错误modelName:%s", testCase.getName());
            }
        }
        return JSONSerializerUtil.copy(XmlModel.builder()
                .headers(testCase.getHeaders())
                .name(testCase.getName())
                .url(testCase.getUrl())
                .description(testCase.getDescription())
                .method(testCase.getMethod())
                .xmlData(MapToXml.toXml((Map<String, Object>) testCase.getRequests()))
                .validate(testCase.getValidate())
                .setup(testCase.getSetup())
                .saveMethod(testCase.getSaveMethod())
                .saveClass(testCase.getSaveClass())
                .saveGlobal(testCase.getSaveGlobal())
                .saveThread(testCase.getSaveThread())
                .onFailure(testCase.getOnFailure())
                .teardown(testCase.getTeardown())
                .build(), XmlModel.class);
    }

    public static FormModel toFormModel(TestCase testCase) {
        if (Objects.nonNull(testCase.getRequests())) {
            if (!(testCase.getRequests() instanceof Map)) {
                //form请求数组格式?
                throw new BizException("Requests格式错误,当type为form时必须为键值对, 错误modelName:%s", testCase.getName());
            }
        }
        return JSONSerializerUtil.copy(FormModel.builder()
                .headers(testCase.getHeaders())
                .name(testCase.getName())
                .url(testCase.getUrl())
                .description(testCase.getDescription())
                .method(testCase.getMethod())
                .formData((Map<String, Object>) testCase.getRequests())
                .validate(testCase.getValidate())
                .setup(testCase.getSetup())
                .saveMethod(testCase.getSaveMethod())
                .saveClass(testCase.getSaveClass())
                .saveGlobal(testCase.getSaveGlobal())
                .saveThread(testCase.getSaveThread())
                .onFailure(testCase.getOnFailure())
                .teardown(testCase.getTeardown())
                .build(), FormModel.class);
    }

    public static TextModel toTextModel(TestCase testCase) {
        String textData = null;
        if (Objects.nonNull(testCase.getRequests())) {
            if (testCase.getRequests() instanceof List || testCase.getRequests() instanceof Collection) {
                textData = JSONSerializerUtil.serialize(testCase.getRequests());
            } else {
                textData = String.valueOf(testCase.getRequests());
            }
        }
        return JSONSerializerUtil.copy(TextModel.builder()
                .headers(testCase.getHeaders())
                .name(testCase.getName())
                .url(testCase.getUrl())
                .description(testCase.getDescription())
                .method(testCase.getMethod())
                .textData(textData)
                .validate(testCase.getValidate())
                .setup(testCase.getSetup())
                .saveMethod(testCase.getSaveMethod())
                .saveClass(testCase.getSaveClass())
                .saveGlobal(testCase.getSaveGlobal())
                .saveThread(testCase.getSaveThread())
                .onFailure(testCase.getOnFailure())
                .teardown(testCase.getTeardown())
                .build(), TextModel.class);
    }
}
