package com.ly.plugin;
import com.ly.api.TestApiClient;
import com.ly.core.base.Response;
import com.ly.core.exception.BizException;
import com.ly.core.parse.BaseModel;
import com.ly.core.parse.FormModel;
import com.ly.core.parse.JsonModel;
import com.ly.core.parse.TestCase;
import com.ly.core.parse.YmlParse;
import com.ly.core.redis.RedisService;
import com.ly.core.utils.AssertUtils;
import com.ly.core.utils.ContextDataStorage;
import com.ly.core.utils.JSONSerializerUtil;
import com.ly.core.utils.TestCase2BaseModel;
import com.ly.core.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 该类所有方法都可加入data.yml中validate中的plugin
 * plugin支持jsonpath与${}
 * @Author: luoy
 * @Date: 2020/4/26 16:13.
 */
@Component
public class PluginSupport {
    @Autowired
    private TestApiClient testApiClient;

    private ContextDataStorage saveData = ContextDataStorage.getInstance();


    public boolean isTest(){
        return false;
    }

    public void createTimestamp() {
        saveData.setMethodAttribute("timestamp", String.valueOf(System.currentTimeMillis()));
    }

    public void setUptest(BaseModel model) {
        System.out.println("setUptest:" + model.getRequests());
    }

    public void teardowm(String arg1, String arg2) {
        System.out.println("teardowm:" + arg1 + "," +  arg2);
    }

    public void teardowm1(Response response, String arg2) {
        System.out.println("teardowm1:" +  arg2);
        System.out.println("teardowm1-url:" +  response.getRequests().getUrl());
    }

    /**
     * 生成length位随机数,存入缓存
     * @param length
     */
    public void randomInt(int length) {
        saveData.setAttribute("Random10", Utils.getRandomInt(length));
    }

    /**
     * 生成orderid,存入缓存
     */
    public void randomAmapOrderId() {
        saveData.setMethodAttribute("randomOrderId", Utils.getRandomInt(10));
    }

    /**
     * 生成当前localDateTime,存入缓存
     */
    public void localDateTime() {
        saveData.setAttribute("LocalDateTime", Utils.getLocalDataTime());
    }
}
