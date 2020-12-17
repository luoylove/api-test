package com.ly.core.notification;

/**
 * @Author: luoy
 * @Date: 2019/9/16 13:25.
 */
public interface BaseNotificationServer {

    void notification(NotificationRequest request) throws Exception;
}
