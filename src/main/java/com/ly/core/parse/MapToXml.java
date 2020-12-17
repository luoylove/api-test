package com.ly.core.parse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

/**
 * @Author: luoy
 * @Date: 2020/11/27 10:50.
 */
public class MapToXml {
    public static String toXml(Map<String, Object> map) {
        final String statement = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        final String defaultRootElement = "xml";

        Document document = DocumentHelper.createDocument();
        document.addElement(defaultRootElement);
        Element element =  recursionMapToXml(document.getRootElement(), map);
        if (map.size() == 1) {
            return statement + element.element(map.keySet().stream().findFirst().get()).asXML();
        }
        return statement + element.asXML();
    }

    private static Element recursionMapToXml(Element element, Map<String, Object> map) {
        for (String key: map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Map) {
                Element childElement = element.addElement(key);
                recursionMapToXml(childElement, (Map<String, Object>)value);
            } else if (value instanceof List) {
                Element childElement = element.addElement(key);
                for (Object l : (List) value) {
                    if (l instanceof Map) {
                        recursionMapToXml(childElement, (Map<String, Object>)l);
                    }
                }
            } else {
                element.addElement(key).addText(value == null ? "" : String.valueOf(value));
            }
        }
        return element;
    }
}
