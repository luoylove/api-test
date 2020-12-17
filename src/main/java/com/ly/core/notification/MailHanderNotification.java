package com.ly.core.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: luoy
 * @Date: 2019/9/16 15:00.
 */
@Service
@Slf4j
public class MailHanderNotification extends HandlerNotification{
    @Autowired
    private MailNotificationServerImpl mailNotificationServer;

    @Override
    public void notification() {
        MailNotificationRequest mailNotificationRequest = MailNotificationRequest.builder()
                .subject("测试用例运行完毕")
                .context("测试用例运行完毕，请检查！")
                .build();
        try {
            mailNotificationServer.notification(mailNotificationRequest);
        } catch (Exception e) {
            log.error("邮件发送失败", e);
        }
    }
}
