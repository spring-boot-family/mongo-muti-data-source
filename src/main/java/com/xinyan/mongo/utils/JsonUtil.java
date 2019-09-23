package com.xinyan.mongo.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    private static ObjectMapper defaultObjectMapper = new ObjectMapper();
    private static ObjectMapper ignoreUnknownObjectMapper = new ObjectMapper();

    private JsonUtil() {
        throw new IllegalAccessError("Utility class");
    }

    public static <T> T parse(String content, Class<T> t) {
        try {
            return ignoreUnknownObjectMapper.readValue(content, t);
        } catch (IOException var3) {
            LOGGER.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static <T> T parseWithCheck(String content, Class<T> t) {
        try {
            return defaultObjectMapper.readValue(content, t);
        } catch (IOException var3) {
            LOGGER.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static <T> T parse(String content, TypeReference<T> t) {
        try {
            return ignoreUnknownObjectMapper.readValue(content, t);
        } catch (IOException var3) {
            LOGGER.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static <T> T parseWithCheck(String content, TypeReference<T> t) {
        try {
            return defaultObjectMapper.readValue(content, t);
        } catch (IOException var3) {
            LOGGER.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static String toString(Object t) {
        try {
            return defaultObjectMapper.writeValueAsString(t);
        } catch (JsonProcessingException var2) {
            LOGGER.error(var2.getMessage(), var2);
            return "";
        }
    }

    public static <T> List<T> toList(String str) {
        try {
            return (List)ignoreUnknownObjectMapper.readValue(str, new TypeReference<List<T>>() {
            });
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> toListWithCheck(String str) {
        try {
            return (List)defaultObjectMapper.readValue(str, new TypeReference<List<T>>() {
            });
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static Map toMap(String str) {
        try {
            return (Map)defaultObjectMapper.readValue(str, new TypeReference<Map>() {
            });
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public static List parseList(String str) {
        List list = (List)parse(str, List.class);
        return list != null && !list.isEmpty() ? list : Collections.emptyList();
    }

    static {
        ignoreUnknownObjectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        ignoreUnknownObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        ignoreUnknownObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}

