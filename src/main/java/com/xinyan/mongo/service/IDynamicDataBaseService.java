package com.xinyan.mongo.service;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 根据月份动态获取数据库保存数据
 *
 * @author weimin_ruan
 * @date 2019/9/23
 */
public interface IDynamicDataBaseService {
    /**
     * 根据月份动态获取数据库保存数据
     *
     * @param object
     * @param databaseName
     * @param defaultCollection
     * @param month
     */
    void saveDataByPhone(Object object, String databaseName, String defaultCollection, String month, String mobile);

    void saveDataByToken(Object object, String databaseName, String defaultCollection, String month, String token);

    /**
     * 根据手机查询分库数据
     *
     * @param databaseName
     * @param collection
     * @param mobile
     * @param month
     * @param entityClass
     * @param <T>
     * @return
     */
    <T> T queryDataByPhone(String databaseName, String collection, String mobile, String month, Class<T> entityClass);

    /**
     * 根据token查询分库数据
     *
     * @param databaseName
     * @param collection
     * @param token
     * @param month
     * @param entityClass
     * @param <T>
     * @return
     */
    <T> T queryDataByToken(String databaseName, String collection, String token, String month, Class<T> entityClass);

    <T> T queryLastDataByPhone(String databaseName, String collection, String phone, String month, Class<T> entityClass);

    /**
     * 根据月份获取MongoTemplate
     *
     * @param databaseName
     * @param collectionName
     * @param month
     * @return
     */
    MongoTemplate getMongoTemplateByMonth(String databaseName, String collectionName, String month);

    MongoTemplate getMongoTemplateByMonthWithVersion(String databaseName, String collectionName, String month, String version);

    /**
     * 获取通用 MongoTemplate
     *
     * @param databaseName
     * @param collectionName
     * @return
     */
    MongoTemplate getMongoTemplate(String databaseName, String collectionName);
}

