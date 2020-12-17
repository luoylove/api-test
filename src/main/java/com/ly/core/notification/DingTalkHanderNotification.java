package com.ly.core.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: luoy
 * @Date: 2019/9/16 15:04.
 */
@Service
public class DingTalkHanderNotification extends HandlerNotification{
    @Autowired
    private DingTalkNotificationSererImpl dingTalkNotificationSererImpl;

    @Override
    public void notification() {
        DingTalkNotificationRequest dingTalkRequest = DingTalkNotificationRequest.builder()
                .msgtype(DingTalkNotificationSererImpl.MsgType.text)
                .text(DingTalkNotificationRequest.Text.builder().content("测试用例运行完毕，请检查！").build())
                .build();
        dingTalkNotificationSererImpl.notification(dingTalkRequest);
    }
}
