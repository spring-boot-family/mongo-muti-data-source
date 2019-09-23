package com.xinyan.mongo.utils;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.DBObject;
import com.xinyan.mongo.model.DataMongoObject;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
@Slf4j
public class DataMongoObjectUtil {
    private static final List<String> EXCLUDE_FIELDS = Arrays.asList("log", "serialVersionUID", "_class", "id", "_id");

    /***
     * DBObject转成DataMongoObject
     * @param dbObject
     * @return
     */
    public static DataMongoObject toDataMongoObject(DBObject dbObject) {
        if (null == dbObject) {
            return null;
        }
        try {
            int fieldCount = 6;
            int size = dbObject.keySet().size();
            if (size <= fieldCount) {
                log.warn("size:{}", size);
            }
            DataMongoObject dataMongoObject = new DataMongoObject();
            dataMongoObject.setId(dbObject.get("_id").toString());
            dataMongoObject.setData(dbObject.get("data"));
            dataMongoObject.setSourceName(StringUtil.isEmpty(dbObject.get("sourceName")) ? null : dbObject.get("sourceName").toString());
            dataMongoObject.setSourceDesc(StringUtil.isEmpty(dbObject.get("sourceDesc")) ? null : dbObject.get("sourceDesc").toString());
            dataMongoObject.setUpdateTime(StringUtil.isEmpty(dbObject.get("updateTime")) ? null : dbObject.get("updateTime").toString());
            dataMongoObject.setToken(StringUtil.isEmpty(dbObject.get("token")) ? null : dbObject.get("token").toString());
            dataMongoObject.setDataType(StringUtil.isEmpty(dbObject.get("dataType")) ? null : dbObject.get("dataType").toString());
            return dataMongoObject;
        } catch (Exception e) {
            log.error("token:{}" + e.getMessage(), dbObject.get("token"), e);
            return null;
        }
    }

    /***
     *
     * 报错 将直接保存失败  去除id字段
     * DataMongoObject转成BasicDBObject
     * @param dataMongoObject
     * @return
     */
    public static Document toDocumentWithoutId(Object dataMongoObject) {
        Document document = Document.parse(StringUtil.toJsonWithReplaceClass(dataMongoObject));
        document.remove("id");
        return document;
    }

    /**
     * document to DataMongoObject
     *
     * @param document
     * @return
     */
    public static DataMongoObject toDataMongoObject(Document document) {
        return toMongoObject(document, DataMongoObject.class);
    }


    /**
     * document to DataMongoObject
     *
     * @param document
     * @returnT
     */
    public static <T> T toMongoObject(Document document, Class<T> cls) {
        if (null == document) {
            return null;
        }
        document.remove("_id");
        document.remove("_class");
        return JsonUtil.parse(StringUtil.toJsonWithReplaceClass(document), cls);
    }

    /***
     * data转成Map
     * @param data
     * @return
     */
    public static Map<String, Object> toMap(Object data) {
        Map<String, Object> map = new HashMap<>();
        Field[] declaredFields = data.getClass().getDeclaredFields();
        for (Field f : declaredFields) {
            String name = f.getName();
            if (!EXCLUDE_FIELDS.contains(name)) {
                f.setAccessible(true);
                try {
                    map.put(name, f.get(data));
                } catch (IllegalAccessException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return map;
    }

    /***
     * data转成document
     * @param data
     * @return
     */
    public static Document toDocument(Object data) {
        return new Document(toMap(data));
    }

    /**
     * document to DataMongoObject
     *
     * @param object
     * @returnT
     */
    public static Object toMongoObject(Object object) {
        if (null == object) {
            return null;
        }
        String jsonString = JSONObject.toJSONString(object);
        log.info("jsonString1:{}", jsonString);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        jsonObject.remove("id");
        log.info("jsonString2:{}", jsonObject.toJSONString());
        return JSONObject.parse(jsonObject.toJSONString());
    }

    public static <T> T unpack(DataMongoObject body, Class<T> type) {
        if (body == null || body.getData() == null) {
            return null;
        }
        return JsonUtil.parse(JsonUtil.toString(body.getData()), type);
    }
}

