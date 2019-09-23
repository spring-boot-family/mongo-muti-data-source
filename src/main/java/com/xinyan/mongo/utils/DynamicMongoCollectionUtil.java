package com.xinyan.mongo.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态mongo数据库连接
 * @author weimin_ruan
 * @date 2019/9/23
 */
@Slf4j
@Component
public class DynamicMongoCollectionUtil {

    //@Value("${mongo.database.report.report-carrier-contact}")
    private String contactDatabaseName;

    private final String PHONE = "phone";
    private final String CREATE_TIME = "createTime";
    private final String UPDATE_TIME = "updateTime";
    private final String TOKEN = "token";
    private final String INDEX_SEPARATOR = "_";
    private final Integer INDEX_ASC = 1;
    private final Integer INDEX_DESC = -1;
    private final String idxTokenCreateTime = TOKEN + INDEX_SEPARATOR + INDEX_ASC + INDEX_SEPARATOR + CREATE_TIME + INDEX_SEPARATOR + INDEX_DESC;
    private final String idxTokenUpdateTime = TOKEN + INDEX_SEPARATOR + INDEX_ASC + INDEX_SEPARATOR + UPDATE_TIME + INDEX_SEPARATOR + INDEX_DESC;
    private final String idxPhoneUpdateTime = PHONE + INDEX_SEPARATOR + INDEX_ASC + INDEX_SEPARATOR + UPDATE_TIME + INDEX_SEPARATOR + INDEX_DESC;
    private final String idxToken = TOKEN + INDEX_SEPARATOR + INDEX_ASC;

    @Autowired
    private MongoClient mongoClient;
    private ConcurrentHashMap<String, MongoTemplate> mongoTemplateConcurrentHashMap = new ConcurrentHashMap<>();

    @Bean
    public MongoClient mongoClient() {
        return new MongoClient("127.0.0.1", 27017);
    }

    /**
     * 动态获取mongo数据库连接
     *
     * @param mongoDatabaseName   数据库名称
     * @param mongoCollectionName 表名称
     * @return
     */
    public MongoTemplate getDynamicMongoTemplate(String mongoDatabaseName, String mongoCollectionName) {
        MongoTemplate mongoTemplate = mongoTemplateConcurrentHashMap.get(mongoDatabaseName);
        if (mongoTemplate != null) {
            return mongoTemplate;
        }
        mongoTemplate = getMongoTemplate(mongoDatabaseName, mongoCollectionName);
        mongoTemplateConcurrentHashMap.put(mongoDatabaseName, mongoTemplate);
        return mongoTemplate;
    }

    /**
     * 获取mongo数据库连接
     *
     * @param mongoDatabaseName   数据库名称
     * @param mongoCollectionName 表名称
     * @return
     */
    public MongoTemplate getMongoTemplate(String mongoDatabaseName, String mongoCollectionName) {
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(mongoClient, mongoDatabaseName);
        MongoDatabase mongoDatabase = simpleMongoDbFactory.getDb();
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(mongoCollectionName);
        // 根据库名判断是否创建索引，对非运营商不创建索引
        String carrierDatabasePrefix = "carrier";
        String reportDatabasePrefix = "report";
        if (mongoDatabaseName.contains(CarrierModule.NET.getName().toLowerCase()) ||
                mongoDatabaseName.contains(CarrierModule.SMS.getName().toLowerCase()) ||
                mongoDatabaseName.contains(CarrierModule.CALL.getName().toLowerCase())) {
            createIndex(mongoCollection, false);
        } else if (mongoDatabaseName.startsWith(carrierDatabasePrefix)) {
            createIndex(mongoCollection, true);
        } else if (contactDatabaseName.equals(mongoDatabaseName)) {
            createTokenUpdateTimeIndex(mongoCollection);
        } else if (mongoDatabaseName.startsWith(reportDatabasePrefix)) {
            createTokenCreateTimeIndex(mongoCollection);
        }
        MongoCustomConversions conversions = new MongoCustomConversions(Collections.emptyList());
        MongoMappingContext context = new MongoMappingContext();
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        context.afterPropertiesSet();
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(simpleMongoDbFactory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        mappingConverter.setCustomConversions(conversions);
        mappingConverter.afterPropertiesSet();
        return new MongoTemplate(simpleMongoDbFactory, mappingConverter);
    }

    /**
     * 创建运营商索引
     *
     * @param mongoCollection
     */
    private void createIndex(MongoCollection<Document> mongoCollection, boolean hasToken) {
        Set<String> indexSet = getIndexSet(mongoCollection);
        IndexOptions indexOptions = new IndexOptions();
        indexOptions.background(true);
        if (!indexSet.contains(idxPhoneUpdateTime)) {
            Document document = new Document(PHONE, INDEX_ASC);
            document.put(UPDATE_TIME, INDEX_DESC);
            String createIndexResult = mongoCollection.createIndex(document, indexOptions);
            log.info("idx create result:{}", createIndexResult);
        }
        if (hasToken && !indexSet.contains(idxToken)) {
            String createIndexResult = mongoCollection.createIndex(new Document(TOKEN, INDEX_ASC), indexOptions);
            log.info("idx create result:{}", createIndexResult);
        }
    }
    /**
     * 创建报告索引
     *
     * @param mongoCollection
     */
    private void createTokenCreateTimeIndex(MongoCollection<Document> mongoCollection) {
        Set<String> indexSet = getIndexSet(mongoCollection);
        IndexOptions indexOptions = new IndexOptions();
        indexOptions.background(true);
        if (!indexSet.contains(idxTokenCreateTime)) {
            Document document = new Document(TOKEN, INDEX_ASC);
            document.put(CREATE_TIME, INDEX_DESC);
            String createIndexResult = mongoCollection.createIndex(document, indexOptions);
            log.info("idx create result:{}", createIndexResult);
        }
        if (!indexSet.contains("mobile_1_createTime_-1")) {
            Document document = new Document("mobile", INDEX_ASC);
            document.put(CREATE_TIME, INDEX_DESC);
            String createIndexResult = mongoCollection.createIndex(document, indexOptions);
            log.info("idx create result:{}", createIndexResult);
        }
    }
    /**
     * 创建通讯录索引
     *
     * @param mongoCollection
     */
    private void createTokenUpdateTimeIndex(MongoCollection<Document> mongoCollection) {
        Set<String> indexSet = getIndexSet(mongoCollection);
        IndexOptions indexOptions = new IndexOptions();
        indexOptions.background(true);
        if (!indexSet.contains(idxTokenUpdateTime)) {
            Document document = new Document(TOKEN, INDEX_ASC);
            document.put(UPDATE_TIME, INDEX_DESC);
            String createIndexResult = mongoCollection.createIndex(document, indexOptions);
            log.info("idx create result:{}", createIndexResult);
        }
    }
    /**
     * 获取索引
     *
     * @param mongoCollection
     * @return
     */
    private Set<String> getIndexSet(MongoCollection<Document> mongoCollection) {
        ListIndexesIterable<Document> indexes = mongoCollection.listIndexes();
        Set<String> indexSet = new HashSet<>();
        for (Document document : indexes) {
            String indexName = document.get("name").toString();
            indexSet.add(indexName);
        }
        log.info("index info:{}", indexSet);
        return indexSet;
    }

}
