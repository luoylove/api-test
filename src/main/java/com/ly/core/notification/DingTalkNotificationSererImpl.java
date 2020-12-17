package com.ly.core.notification;

import com.ly.headersfilter.RestAssuredLogFilter;
import com.ly.core.restassured.RestassuredHttpHandleBuilder;
import com.ly.core.utils.JSONSerializerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Author: luoy
 * @Date: 2019/9/12 14:35.
 */
@Service
@Slf4j
public class DingTalkNotificationSererImpl implements BaseNotificationServer {
    @Value("${notification.dingtalk.url}")
    private String url;

    @Override
    public void notification(NotificationRequest request) {
        String requestJson = JSONSerializerUtil.serialize(request);
        log.info("钉钉通知=============");
        RestassuredHttpHandleBuilder.create().post(requestJson, url, new RestAssuredLogFilter());
    }

    public enum MsgType {
        text,link,markdown,@Deprecated actionCard,@Deprecated feedCard
    }
}
