package com.jinninghui.datasphere.icreditstudio.framework.utils;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 描述 ：反射工具类
 * Created by lidab on 2017/9/18.
 */
public class ReflectUtils {

    private static final String[][] EMPTY_STRING_ARRAY = new String[0][];

    /**
     * Copy the property values of the given source bean into the target bean. 
     * Note: The source and target classes do not have to match or even be derived from each other, 
     * as long as the properties match. Any bean properties that the source bean exposes but the target bean does not will silently be ignored. 
     *
     * @param dest 目标类
     * @param orig 来源
     */
    public static void copyProperties(Object dest, Object orig) throws InvocationTargetException, IllegalAccessException {
        BeanUtils.copyProperties(orig,dest);
    }
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


    public static Object getPropertyValue(String propertyName, Object instance) {
        try {
            Class<?> cls = instance.getClass();
            Field field = cls.getDeclaredField(propertyName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Map<String, Object> getPropertyValues(Object instance) {
        return getPropertyValues(instance, true);
    }

    public static Map<String, Object> getPropertyValues(Object instance, boolean containsNullProperty) {
        Map<String, Object> values = new LinkedHashMap<>();
        Class<?> cls = instance.getClass();
        Field[] fields = cls.getDeclaredFields();

        try {
            for (Field field : fields) {
                String fieldName = field.getName();
                if ((fieldName == null || !fieldName.contains("$")) && PropertyUtils.isReadable(instance, fieldName)) {
                    assert fieldName != null;
                    Method method = cls.getDeclaredMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
                    Object value = method.invoke(instance);
                    if (!containsNullProperty) {
                        if (value != null) {
                            values.put(fieldName, value);
                        }
                    } else {
                        values.put(fieldName, value);
                    }
                }
            }

            return values;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Map<String, Object> getNotNullPropertyValues(Object instance) {
        return getPropertyValues(instance, false);
    }

    public static void setPropertyValue(String propertyName, Object propertyValue, Object instance) {
        try {
            Class<?> cls = instance.getClass();
            String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            Method method = cls.getDeclaredMethod(methodName, propertyValue.getClass());
            if (method != null) {
                method.invoke(instance, propertyValue);
            } else {
                throw new IllegalStateException("bean have not setter method");
            }
        } catch (Exception e) {
            throw new IllegalStateException((e));
        }
    }

    public static boolean isValueNull(String propertyName, Object instance) {
        return getPropertyValue(propertyName, instance) == null;
    }

    public static void copyPropertiesFromMap(Object instance, Map<String, Object> values) {
        copyPropertiesFromMap(instance, values, false, EMPTY_STRING_ARRAY);
    }

    public static void copyPropertiesFromMap(Object instance, Map<String, Object> source, String[][] mapping) {
        if (source != null && source.size() > 0) {
            copyPropertiesFromMap(instance, source);
            if (mapping != null && mapping.length > 0) {
                try {
                    for (String[] item : mapping) {
                        String sourcePropertyName = item[0];
                        String targetPropertyName = item[1];
                        if (source.containsKey(sourcePropertyName)) {
                            PropertyUtils.setProperty(instance, targetPropertyName, source.get(sourcePropertyName));
                        }
                    }
                } catch (Exception e) {
                    throw new IllegalStateException((e));
                }
            }
        }

    }

    public static void copyPropertiesFromMap(Object instance, Map<String, Object> values, boolean isTable2Bean, String[][] mapping) {
        if (values != null && values.size() != 0) {
            try {

                for (Map.Entry<String, Object> current : values.entrySet()) {
                    String name = isTable2Bean ? StringUtils.formatVariableWithLower(current.getKey()) : current.getKey();
                    Object value = current.getValue();
                    if (value != null) {
                        BeanUtils.setProperty(instance, name, value);
                    }
                }

                if (mapping != null && mapping.length > 0) {
                    for (String[] item : mapping) {
                        String sourcePropertyName = item[0];
                        String targetPropertyName = item[1];
                        if (values.containsKey(sourcePropertyName)) {
                            PropertyUtils.setProperty(instance, targetPropertyName, values.get(sourcePropertyName));
                        }
                    }
                }

            } catch (Exception e) {
                throw new IllegalStateException((e));
            }
        }
    }

    public static void copyTableData2Bean(Object instance, Map<String, Object> values) {
        copyPropertiesFromMap(instance, values, true, EMPTY_STRING_ARRAY);
    }

    public static void copyTableData2Bean(Object instance, Map<String, Object> values, String[][] mapping) {
        copyPropertiesFromMap(instance, values, true, mapping);
    }

    public static void copyPropertiesFromBean(Object sourceBean, Object targetBean) {
        copyPropertiesFromBean(sourceBean, targetBean, EMPTY_STRING_ARRAY);
    }

    public static void copyPropertiesFromBean(Object sourceBean, Object targetBean, String[][] mapping) {
        try {
            BeanUtils.copyProperties(targetBean, sourceBean);
            if (mapping != null && mapping.length > 0) {

                for (String[] item : mapping) {
                    String source = item[0].trim();
                    String target = item[1].trim();
                    Object value = PropertyUtils.getProperty(sourceBean, source);
                    if (value != null) {
                        PropertyUtils.setProperty(targetBean, target, value);
                    }
                }
            }

        } catch (Exception e) {
            throw new IllegalStateException((e));
        }
    }

    public static <T> T createAndCopyProperties(Object sourceBean, Class<T> type) {
        return createAndCopyProperties(sourceBean, type, EMPTY_STRING_ARRAY);
    }

    public static <T> T createAndCopyProperties(Object sourceBean, Class<T> type, String[][] mapping) {
        try {
            T instance = type.newInstance();
            copyPropertiesFromBean(sourceBean, instance);
            if (mapping != null && mapping.length > 0) {
                for (String[] item : mapping) {
                    String source = item[0].trim();
                    String target = item[1].trim();
                    Object value = PropertyUtils.getProperty(sourceBean, source);
                    if (value != null) {
                        PropertyUtils.setProperty(instance, target, value);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            throw new IllegalStateException((e));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T simpleClone(Object source, Class<T> type) {
        try {
            return (T) BeanUtils.cloneBean(source);
        } catch (Exception e) {
            throw new IllegalStateException((e));
        }
    }

    public static Map bean2Map(Object source) {
        Map<Object, Object> map = new HashMap<>();
        map.putAll(new BeanMap(source));
        map.remove("class");
        return map;
    }

    public static Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
            default:
                return classType;
        }
    }

    public static Class<?> getClassType(String className) throws ClassNotFoundException {
        switch (className) {
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            case "char":
                return Character.class;
            case "boolean":
                return Boolean.class;
            case "short":
                return Short.class;
            case "byte":
                return Byte.class;
            default:
                return Class.forName(className);
        }
    }
}
