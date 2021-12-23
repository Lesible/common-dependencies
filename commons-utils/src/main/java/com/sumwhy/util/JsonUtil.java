package com.sumwhy.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p> @date: 2021-06-08 20:11</p>
 *
 * @author 何嘉豪
 */
public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Map<String, JavaType> TYPE_MAPPING = new ConcurrentHashMap<>();
    private static final ObjectMapper FAST_JSON_LIKE_OBJECT_MAPPER = new ObjectMapper();
    private static final TypeFactory TYPE_FACTORY = OBJECT_MAPPER.getTypeFactory();


    static {
        OBJECT_MAPPER.findAndRegisterModules();
        OBJECT_MAPPER.setTimeZone(TimeZone.getDefault());
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        FAST_JSON_LIKE_OBJECT_MAPPER.findAndRegisterModules();
        FAST_JSON_LIKE_OBJECT_MAPPER.setTimeZone(TimeZone.getDefault());
        FAST_JSON_LIKE_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        FAST_JSON_LIKE_OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    private JsonUtil() {
    }

    /**
     * 使用默认的 objectMapper 转换为 jsonStr
     *
     * @param o 对象
     * @return jsonStr
     */
    public static String jsonValue(Object o) {
        return jsonValue(o, OBJECT_MAPPER);
    }

    /**
     * 使用指定的 objectMapper 转换为 jsonStr
     *
     * @param o            对象
     * @param objectMapper 指定转换规则的 objectMapper
     * @return jsonStr
     */
    public static String jsonValue(Object o, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("转换为 json 出错,对象信息:{}", o, e);
            return "{}";
        }
    }

    /**
     * 深拷贝
     *
     * @param source           来源
     * @param parametrized     范型基本类
     * @param parameterClasses 范型上面的类
     * @param <T>              Class 的范型
     * @return 拷贝后的结果
     */
    public static <T> T deepClone(Object source, Class<?> parametrized, Class<?>... parameterClasses) {
        JavaType javaType = constructType(parametrized, parameterClasses);
        return OBJECT_MAPPER.convertValue(source, javaType);
    }

    /**
     * 深拷贝
     *
     * @param source   来源
     * @param javaType 类型
     * @param <T>      Class 的范型
     * @return 拷贝后的结果
     */
    public static <T> T deepClone(Object source, JavaType javaType) {
        return OBJECT_MAPPER.convertValue(source, javaType);
    }

    /**
     * 使用 jackson 更新数据
     *
     * @param source 来源
     * @param target 目标
     * @param <T>    Class 的范型
     * @return 更新后的数据
     */
    public static <T> T updateValue(Object source, T target) {
        try {
            return OBJECT_MAPPER.updateValue(target, source);
        } catch (JsonMappingException e) {
            log.error("尝试使用 source: {} 更新数据失败", source, e);
        }
        return target;
    }

    // ---------- 解析 json-------------

    /**
     * 简单的内置字符串判空
     *
     * @param str 字符串
     * @return 是否为空 true 为空
     */
    private static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 使用指定的 objectMapper 转换 jsonStr 为对象
     *
     * @param jsonStr      jsonStr
     * @param objectMapper 指定转换规则的 objectMapper
     * @param clazz        类型
     * @param <T>          Class 的范型
     * @return 转换后的对象
     */
    public static <T> T parseJson(String jsonStr, ObjectMapper objectMapper, Class<T> clazz) {
        if (isEmpty(jsonStr)) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (JsonProcessingException e) {
            log.error("解析 json 出错,jsonStr:{}", jsonStr, e);
            return null;
        }
    }

    /**
     * 使用指定的 objectMapper 转换 jsonStr 为对象
     *
     * @param jsonStr      jsonStr
     * @param objectMapper 指定转换规则的 objectMapper
     * @param javaType     类型
     * @param <T>          Class 的范型
     * @return 转换后的对象
     */
    public static <T> T parseJson(String jsonStr, ObjectMapper objectMapper, JavaType javaType) {
        if (isEmpty(jsonStr)) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonStr, javaType);
        } catch (JsonProcessingException e) {
            log.error("解析 json 出错,jsonStr:{}", jsonStr, e);
            return null;
        }
    }

    /**
     * 使用指定的 objectMapper 转换 jsonStr 为对象
     *
     * @param jsonStr       jsonStr
     * @param objectMapper  指定转换规则的 objectMapper
     * @param referenceType 类型
     * @param <T>           Class 的范型
     * @return 转换后的对象
     */
    public static <T> T parseJson(String jsonStr, ObjectMapper objectMapper, ReferenceType referenceType) {
        if (isEmpty(jsonStr)) {
            return null;
        }
        try {
            return objectMapper.readValue(jsonStr, referenceType);
        } catch (JsonProcessingException e) {
            log.error("解析 json 出错,jsonStr:{}", jsonStr, e);
            return null;
        }
    }

    /**
     * 使用指定的 objectMapper 转换 jsonStr 为对象
     *
     * @param jsonStr          jsonStr
     * @param objectMapper     指定转换规则的 objectMapper
     * @param parametrized     基本类型
     * @param parameterClasses 范型类型
     * @param <T>              Class 的范型
     * @return 转换后的对象
     */
    public static <T> T parseJson(String jsonStr, ObjectMapper objectMapper, Class<?> parametrized, Class<?>... parameterClasses) {
        JavaType javaType = constructType(parametrized, parameterClasses);
        return parseJson(jsonStr, objectMapper, javaType);
    }

    /**
     * 使用指定的 objectMapper 转换 jsonStr 为 ArrayList
     *
     * @param jsonStr      jsonStr
     * @param objectMapper 指定转换规则的 objectMapper
     * @param clazz        范型类型
     * @param <T>          list 的范型
     * @return 转换后的对象
     */
    public static <T> List<T> parseList(String jsonStr, ObjectMapper objectMapper, Class<T> clazz) {
        JavaType javaType = constructCollectionType(ArrayList.class, clazz);
        return parseJson(jsonStr, objectMapper, javaType);
    }

    /**
     * 使用指定的 objectMapper 转换 jsonStr 为 ArrayList
     *
     * @param jsonStr      jsonStr
     * @param objectMapper 指定转换规则的 objectMapper
     * @param javaType     范型类型
     * @param <T>          list 的范型
     * @return 转换后的对象
     */
    public static <T> List<T> parseList(String jsonStr, ObjectMapper objectMapper, JavaType javaType) {
        JavaType bindType = constructCollectionType(ArrayList.class, javaType);
        return parseJson(jsonStr, objectMapper, bindType);
    }

    /**
     * 使用指定的 objectMapper 转换 jsonStr 为 HashMap
     *
     * @param jsonStr      jsonStr
     * @param objectMapper 指定转换规则的 objectMapper
     * @param kClazz       键的类型
     * @param vClazz       值的类型
     * @param <K>          map 键的范型
     * @param <V>          map 值的范型
     * @return 转换后的对象
     */
    public static <K, V> Map<K, V> parseMap(String jsonStr, ObjectMapper objectMapper, Class<K> kClazz, Class<V> vClazz) {
        JavaType mapType = constructMapType(HashMap.class, kClazz, vClazz);
        return parseJson(jsonStr, objectMapper, mapType);
    }

    /**
     * 使用默认的 objectMapper 转换 jsonStr 为指定对象
     *
     * @param jsonStr  jsonStr
     * @param javaType 目标类型
     * @param <T>      范型
     * @return 转换后的对象
     */
    public static <T> T parseJson(String jsonStr, JavaType javaType) {
        return parseJson(jsonStr, OBJECT_MAPPER, javaType);
    }

    /**
     * 使用默认的 objectMapper 转换 jsonStr 为指定对象
     *
     * @param jsonStr       jsonStr
     * @param referenceType 目标类型
     * @param <T>           范型
     * @return 转换后的对象
     */
    public static <T> T parseJson(String jsonStr, ReferenceType referenceType) {
        return parseJson(jsonStr, OBJECT_MAPPER, referenceType);
    }

    /**
     * 使用默认的 objectMapper 转换 jsonStr 为指定对象
     *
     * @param jsonStr jsonStr
     * @param clazz   目标类型
     * @param <T>     范型
     * @return 转换后的对象
     */
    public static <T> T parseJson(String jsonStr, Class<T> clazz) {
        return parseJson(jsonStr, OBJECT_MAPPER, clazz);
    }

    /**
     * 使用默认的 objectMapper 转换 jsonStr 为指定对象
     *
     * @param jsonStr          jsonStr
     * @param parametrized     目标类型
     * @param parameterClasses 目标范型
     * @param <T>              范型
     * @return 转换后的对象
     */
    public static <T> T parseJson(String jsonStr, Class<T> parametrized, Class<?>... parameterClasses) {
        return parseJson(jsonStr, OBJECT_MAPPER, parametrized, parameterClasses);
    }

    /**
     * 使用默认的 objectMapper 转换 jsonStr 为 ArrayList
     *
     * @param jsonStr jsonStr
     * @param clazz   目标类型
     * @param <T>     list 的范型
     * @return 转换后的对象
     */
    public static <T> List<T> parseList(String jsonStr, Class<T> clazz) {
        return parseList(jsonStr, OBJECT_MAPPER, clazz);
    }

    /**
     * 使用默认的 objectMapper 转换 jsonStr 为 ArrayList
     *
     * @param jsonStr  jsonStr
     * @param javaType 目标类型
     * @param <T>      list 的范型
     * @return 转换后的对象
     */
    public static <T> List<T> parseList(String jsonStr, JavaType javaType) {
        return parseList(jsonStr, OBJECT_MAPPER, javaType);
    }

    /**
     * 使用默认的 objectMapper 转换 jsonStr 为 HashMap
     *
     * @param jsonStr jsonStr
     * @param kClazz  键的类型
     * @param vClazz  值的类型
     * @param <K>     map 键的范型
     * @param <V>     map 值的范型
     * @return 转换后的对象
     */
    public static <K, V> Map<K, V> parseMap(String jsonStr, Class<K> kClazz, Class<V> vClazz) {
        return parseMap(jsonStr, OBJECT_MAPPER, kClazz, vClazz);
    }

    // -------------  构建 javaType ----------


    /**
     * 构建基本 javaType
     *
     * @param rawType          基本类
     * @param parameterClasses 范型
     * @return javaType
     */
    public static JavaType constructType(Class<?> rawType, Class<?>... parameterClasses) {
        String name = rawType.getSimpleName();
        if (parameterClasses != null && parameterClasses.length > 0) {
            name = name + "_" +
                    Arrays.stream(parameterClasses).map(Class::getSimpleName).collect(Collectors.joining("_"));
        }
        return TYPE_MAPPING.computeIfAbsent(name, it -> {
            if (parameterClasses == null || parameterClasses.length == 0) {
                return TYPE_FACTORY.constructType(rawType);
            }
            return TYPE_FACTORY.constructParametricType(rawType, parameterClasses);
        });
    }

    /**
     * 构建基本 javaType
     *
     * @param rawType  基本类
     * @param javaType 范型
     * @return javaType
     */
    public static JavaType constructType(Class<?> rawType, JavaType javaType) {
        String name = rawType.getSimpleName();
        if (javaType != null) {
            name = name + "_" + javaType.getTypeName();
        }
        return TYPE_MAPPING.computeIfAbsent(name, it -> {
            if (javaType == null) {
                return TYPE_FACTORY.constructType(rawType);
            }
            return TYPE_FACTORY.constructParametricType(rawType, javaType);
        });
    }

    /**
     * 构建集合 javaType
     *
     * @param colClass 集合类型
     * @param javaType 范型
     * @return javaType
     */
    @SuppressWarnings("rawtypes")
    public static JavaType constructCollectionType(Class<? extends Collection> colClass, JavaType javaType) {
        String name = String.format("col_%s_%s", colClass.getSimpleName(), javaType.getTypeName());
        return TYPE_MAPPING.computeIfAbsent(name, it -> TYPE_FACTORY.constructCollectionType(colClass, javaType));
    }

    /**
     * 构建集合 javaType
     *
     * @param colClass 集合类型
     * @param clazz    范型
     * @return javaType
     */
    @SuppressWarnings("rawtypes")
    public static JavaType constructCollectionType(Class<? extends Collection> colClass, Class<?> clazz) {
        String name = String.format("col_%s_%s", colClass.getSimpleName(), clazz.getSimpleName());
        return TYPE_MAPPING.computeIfAbsent(name, it -> TYPE_FACTORY.constructCollectionType(colClass, clazz));
    }

    /**
     * 构建 map javaType
     *
     * @param mapClass  map 类型
     * @param keyType   键类型
     * @param valueType 值类型
     * @return javaType
     */
    @SuppressWarnings("rawtypes")
    public static JavaType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
        String name = String.format("map_%s_%s_%s", mapClass.getSimpleName(), keyType.getTypeName(), valueType.getTypeName());
        return TYPE_MAPPING.computeIfAbsent(name, it -> TYPE_FACTORY.constructMapType(mapClass, keyType, valueType));
    }

    /**
     * 构建 map javaType
     *
     * @param mapClass   map 类型
     * @param keyClass   键类型
     * @param valueClass 值类型
     * @return javaType
     */
    @SuppressWarnings("rawtypes")
    public static JavaType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        String name = String.format("map_%s_%s_%s", mapClass.getSimpleName(), keyClass.getSimpleName(), valueClass.getSimpleName());
        return TYPE_MAPPING.computeIfAbsent(name, it -> TYPE_FACTORY.constructMapType(mapClass, keyClass, valueClass));
    }

    /**
     * 返回一个可以将 snake_case 转为 camelCase 的 objectMapper
     *
     * @return objectMapper
     */
    public static ObjectMapper fastJsonLikeMapper() {
        return FAST_JSON_LIKE_OBJECT_MAPPER;
    }

}
