package com.ly.core.utils;

import com.ly.core.parse.FormModel;
import com.ly.core.parse.JsonModel;
import com.ly.core.parse.MapToXml;
import com.ly.core.parse.TestCase;
import com.ly.core.parse.XmlModel;

/**
 * 深拷贝
 * @Author: luoy
 * @Date: 2020/5/14 17:55.
 */
public class TestCase2BaseModel {

    public static JsonModel toJsonModel(TestCase testCase) {
        String jsonData = null;
        if(testCase.getRequestsList() != null && testCase.getRequestsList().size() > 0) {
            jsonData = JSONSerializerUtil.serialize(testCase.getRequestsList());
        }
        if(testCase.getRequests() != null && testCase.getRequests().size() > 0) {
            jsonData = JSONSerializerUtil.serialize(testCase.getRequests());
        }
        return JSONSerializerUtil.copy(JsonModel.builder()
                .headers(testCase.getHeaders())
                .name(testCase.getName())
                .url(testCase.getUrl())
                .description(testCase.getDescription())
                .method(testCase.getMethod())
                .jsonData(jsonData)
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
        return JSONSerializerUtil.copy(XmlModel.builder()
                .headers(testCase.getHeaders())
                .name(testCase.getName())
                .url(testCase.getUrl())
                .description(testCase.getDescription())
                .method(testCase.getMethod())
                .xmlData(MapToXml.toXml(testCase.getRequests()))
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
        return JSONSerializerUtil.copy(FormModel.builder()
                .headers(testCase.getHeaders())
                .name(testCase.getName())
                .url(testCase.getUrl())
                .description(testCase.getDescription())
                .method(testCase.getMethod())
                .formData(testCase.getRequests())
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
}
