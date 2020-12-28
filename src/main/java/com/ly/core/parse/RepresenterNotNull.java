package com.ly.core.parse;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

/**
 * @Author: luoy
 * @Date: 2020/12/25 15:29.
 */
public class RepresenterNotNull extends Representer {
    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property,
                                                  Object propertyValue, Tag customTag) {
        if( propertyValue != null) {
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        }
        return null;
    }
}
