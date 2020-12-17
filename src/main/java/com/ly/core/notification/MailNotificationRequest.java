package com.ly.core.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: luoy
 * @Date: 2019/9/16 9:36.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailNotificationRequest extends NotificationRequest{
    /**
     * 主题
     */
    private String subject;

    /**
     * 正文
     */
    private String context;

    /**
     * 发送时间，为空即为立即发送 yyyy-MM-dd HH:mm:ss
     */
    private String date;

}
