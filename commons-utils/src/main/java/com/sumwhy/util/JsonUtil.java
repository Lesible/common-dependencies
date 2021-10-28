package com.sumwhy.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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


    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        FAST_JSON_LIKE_OBJECT_MAPPER.findAndRegisterModules();
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        FAST_JSON_LIKE_OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        FAST_JSON_LIKE_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        FAST_JSON_LIKE_OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    private JsonUtil() {
    }

    public static String jsonValue(Object o, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("转换为 json 出错,对象信息:{}", o, e);
            return "{}";
        }
    }

    public static String jsonValue(Object o) {
        return jsonValue(o, OBJECT_MAPPER);
    }

    public static <T> T parseJson(String json, ObjectMapper objectMapper, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("解析 json 出错,jsonStr:{}", json, e);
            return null;
        }
    }

    public static <T> T parseJson(String json, ObjectMapper objectMapper, JavaType javaType) {
        try {
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("解析 json 出错,jsonStr:{}", json, e);
            return null;
        }
    }

    public static <T> T parseJson(String json, ObjectMapper objectMapper, ReferenceType referenceType) {
        try {
            return objectMapper.readValue(json, referenceType);
        } catch (JsonProcessingException e) {
            log.error("解析 json 出错,jsonStr:{}", json, e);
            return null;
        }
    }

    public static <T> T parseJson(String json, ObjectMapper objectMapper, Class<?> parametrized, Class<?>... parameterClasses) {
        try {
            String name = parametrized.getSimpleName();
            if (parameterClasses != null && parameterClasses.length > 0) {
                name += Arrays.stream(parameterClasses).map(Class::getSimpleName).collect(Collectors.joining("_"));
            }
            JavaType javaType = TYPE_MAPPING.computeIfAbsent(name, it ->
                    objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses));
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("解析 json 出错,jsonStr:{}", json, e);
            return null;
        }
    }

    public static <T> List<T> parseList(String json, ObjectMapper objectMapper, Class<T> clazz) {
        String name = clazz.getSimpleName();
        JavaType javaType = TYPE_MAPPING.computeIfAbsent(name, it -> {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return typeFactory.constructCollectionType(ArrayList.class, clazz);
        });
        return parseJson(json, objectMapper, javaType);
    }

    public static <K, V> Map<K, V> parseMap(String json, ObjectMapper objectMapper, Class<K> kClazz, Class<V> vClazz) {
        String name = String.format("%s_%s", kClazz.getSimpleName(), vClazz.getSimpleName());
        JavaType mapType = TYPE_MAPPING.computeIfAbsent(name, it -> {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return typeFactory.constructMapType(HashMap.class, kClazz, vClazz);
        });
        return parseJson(json, objectMapper, mapType);
    }

    public static ObjectMapper fastJsonLikeMapper() {
        return FAST_JSON_LIKE_OBJECT_MAPPER;
    }

    public static <T> T parseJson(String json, JavaType javaType) {
        return parseJson(json, OBJECT_MAPPER, javaType);
    }

    public static <T> T parseJson(String json, ReferenceType referenceType) {
        return parseJson(json, OBJECT_MAPPER, referenceType);
    }

    public static <T> T parseJson(String json, Class<T> clazz) {
        return parseJson(json, OBJECT_MAPPER, clazz);
    }

    public static <T> T parseJson(String json, Class<T> parametrized, Class<?>... parameterClasses) {
        return parseJson(json, OBJECT_MAPPER, parametrized, parameterClasses);
    }

    public static <T> List<T> parseList(String json, Class<T> clazz) {
        return parseList(json, OBJECT_MAPPER, clazz);
    }

    public static <K, V> Map<K, V> parseMap(String json, Class<K> kClazz, Class<V> vClazz) {
        return parseMap(json, OBJECT_MAPPER, kClazz, vClazz);
    }

}
