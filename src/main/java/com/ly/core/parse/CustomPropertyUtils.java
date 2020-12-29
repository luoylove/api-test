package com.ly.core.parse;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.FieldProperty;
import org.yaml.snakeyaml.introspector.MethodProperty;
import org.yaml.snakeyaml.introspector.MissingProperty;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.util.PlatformFeatureDetector;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CustomPropertyUtils extends PropertyUtils {

    private final Map<Class<?>, Map<String, Property>> propertiesCache = new HashMap<>();
    private final Map<Class<?>, Set<Property>> readableProperties = new HashMap<>();
    private BeanAccess beanAccess = BeanAccess.DEFAULT;
    private boolean allowReadOnlyProperties = false;
    private boolean skipMissingProperties = false;

    private PlatformFeatureDetector platformFeatureDetector;

    public CustomPropertyUtils() {
        this(new PlatformFeatureDetector());
    }

    CustomPropertyUtils(PlatformFeatureDetector platformFeatureDetector) {
        this.platformFeatureDetector = platformFeatureDetector;

        /*
         * Android lacks much of java.beans (including the Introspector class, used here), because java.beans classes tend to rely on java.awt, which isn't
         * supported in the Android SDK. That means we have to fall back on FIELD access only when SnakeYAML is running on the Android Runtime.
         */
        if (platformFeatureDetector.isRunningOnAndroid()) {
            beanAccess = BeanAccess.FIELD;
        }
    }

    @Override
    protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) {
        if (propertiesCache.containsKey(type)) {
            return propertiesCache.get(type);
        }

        Map<String, Property> properties = new LinkedHashMap<String, Property>();
        boolean inaccessableFieldsExist = false;
        switch (bAccess) {
            case FIELD:
                for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                    for (Field field : c.getDeclaredFields()) {
                        int modifiers = field.getModifiers();
                        if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)
                                && !properties.containsKey(field.getName())) {
                            properties.put(field.getName(), new FieldProperty(field));
                        }
                    }
                }
                break;
            default:
                // add JavaBean properties
                try {
                    PropertyDescriptor[] currentProperties = Introspector.getBeanInfo(type)
                            .getPropertyDescriptors();
                    if (type == TestCase.class) {
                        PropertyDescriptor[] propertiesSort = new PropertyDescriptor[8];
                        for(PropertyDescriptor p : currentProperties) {
                            if ("name".equals(p.getName())) {
                                propertiesSort[0] = p;
                            }
                            if ("description".equals(p.getName())) {
                                propertiesSort[1] = p;
                            }
                            if ("type".equals(p.getName())) {
                                propertiesSort[2] = p;
                            }
                            if ("url".equals(p.getName())) {
                                propertiesSort[3] = p;
                            }
                            if ("method".equals(p.getName())) {
                                propertiesSort[4] = p;
                            }
                            if ("headers".equals(p.getName())) {
                                propertiesSort[5] = p;
                            }
                            if ("requests".equals(p.getName())) {
                                propertiesSort[6] = p;
                            }
                            if ("validate".equals(p.getName())) {
                                propertiesSort[7] = p;
                            }
                        }
                        currentProperties = propertiesSort;
                    }

                    for (PropertyDescriptor property : currentProperties) {
                        Method readMethod = property.getReadMethod();
                        if ((readMethod == null || !readMethod.getName().equals("getClass"))
                                && !isTransient(property)) {
                            properties.put(property.getName(), new MethodProperty(property));
                        }
                    }
                } catch (IntrospectionException e) {
                    throw new YAMLException(e);
                }

                // add public fields
                for (Class<?> c = type; c != null; c = c.getSuperclass()) {
                    for (Field field : c.getDeclaredFields()) {
                        int modifiers = field.getModifiers();
                        if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                            if (Modifier.isPublic(modifiers)) {
                                properties.put(field.getName(), new FieldProperty(field));
                            } else {
                                inaccessableFieldsExist = true;
                            }
                        }
                    }
                }
                break;
        }
        if (properties.isEmpty() && inaccessableFieldsExist) {
            throw new YAMLException("No JavaBean properties found in " + type.getName());
        }
        propertiesCache.put(type, properties);
        return properties;
    }

    private static final String TRANSIENT = "transient";

    private boolean isTransient(FeatureDescriptor fd) {
        return Boolean.TRUE.equals(fd.getValue(TRANSIENT));
    }

    @Override
    public Set<Property> getProperties(Class<? extends Object> type) {
        return getProperties(type, beanAccess);
    }

    @Override
    public Set<Property> getProperties(Class<? extends Object> type, BeanAccess bAccess) {
        if (readableProperties.containsKey(type)) {
            return readableProperties.get(type);
        }
        Set<Property> properties = createPropertySet(type, bAccess);
        readableProperties.put(type, properties);
        return properties;
    }

    @Override
    protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
        Set<Property> properties = new LinkedHashSet<>();
        Collection<Property> props = getPropertiesMap(type, bAccess).values();
        for (Property property : props) {
            if (property.isReadable() && (allowReadOnlyProperties || property.isWritable())) {
                properties.add(property);
            }
        }
        return properties;
    }

    @Override
    public Property getProperty(Class<? extends Object> type, String name) {
        return getProperty(type, name, beanAccess);
    }

    @Override
    public Property getProperty(Class<? extends Object> type, String name, BeanAccess bAccess) {
        Map<String, Property> properties = getPropertiesMap(type, bAccess);
        Property property = properties.get(name);
        if (property == null && skipMissingProperties) {
            property = new MissingProperty(name);
        }
        if (property == null) {
            throw new YAMLException(
                    "Unable to find property '" + name + "' on class: " + type.getName());
        }
        return property;
    }

    @Override
    public void setBeanAccess(BeanAccess beanAccess) {
        if (platformFeatureDetector.isRunningOnAndroid() && beanAccess != BeanAccess.FIELD) {
            throw new IllegalArgumentException(
                    "JVM is Android - only BeanAccess.FIELD is available");
        }

        if (this.beanAccess != beanAccess) {
            this.beanAccess = beanAccess;
            propertiesCache.clear();
            readableProperties.clear();
        }
    }

    @Override
    public void setAllowReadOnlyProperties(boolean allowReadOnlyProperties) {
        if (this.allowReadOnlyProperties != allowReadOnlyProperties) {
            this.allowReadOnlyProperties = allowReadOnlyProperties;
            readableProperties.clear();
        }
    }

    @Override
    public boolean isAllowReadOnlyProperties() {
        return allowReadOnlyProperties;
    }

    /**
     * Skip properties that are missing during deserialization of YAML to a Java
     * object. The default is false.
     *
     * @param skipMissingProperties
     *            true if missing properties should be skipped, false otherwise.
     */
    @Override
    public void setSkipMissingProperties(boolean skipMissingProperties) {
        if (this.skipMissingProperties != skipMissingProperties) {
            this.skipMissingProperties = skipMissingProperties;
            readableProperties.clear();
        }
    }

    @Override
    public boolean isSkipMissingProperties() {
        return skipMissingProperties;
    }
}