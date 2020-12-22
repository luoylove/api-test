package com.ly.core.notification;

import com.google.common.collect.Lists;
import com.ly.core.utils.SpringContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationContextHandler {
    private static Map<String, Class> handlerMap = new HashMap<>();

    static{
        handlerMap.put(Channel.DingTalk.getType(), DingTalkNotificationHandler.class);
        handlerMap.put(Channel.Mail.getType(), MailNotificationHandler.class);
    }

    public NotificationHandler getInstance(String type) {
        Class clazz = handlerMap.get(type);

        if (clazz == null) {
            throw new IllegalArgumentException("not found handler for type:" + type);
        }

        return SpringContextUtil.getBean((Class<NotificationHandler>) clazz);
    }

    public List<NotificationHandler> getInstanceAll() {
        List<NotificationHandler> handlers = Lists.newArrayList();

        handlerMap.forEach((k,v) -> handlers.add(SpringContextUtil.getBean((Class<NotificationHandler>) v)));

        return handlers;
    }

    public enum Channel {
        No("0"),DingTalk("1"), Mail("2"),ALL("9");

        private String type;

        Channel(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }
}
