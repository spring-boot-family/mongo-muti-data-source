package com.xinyan.mongo.service;

import com.xinyan.mongo.utils.DynamicMongoCollectionUtil;
import com.xinyan.mongo.utils.MongoTemplateUtil;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
@Slf4j
@Service
public class DynamicDataBaseServiceImpl implements IDynamicDataBaseService {

    @Autowired
    private DynamicMongoCollectionUtil dynamicMongoCollectionUtil;

    /**
     * 根据月份动态获取数据库保存数据
     *
     * @param object         数据对象
     * @param databaseName   数据库名称
     * @param collectionName 数据库表名
     * @param month          数据月份
     * @return
     */
    @Override
    public void saveDataByPhone(Object object, String databaseName, String collectionName, String month, String mobile) {
        MongoTemplate mongoTemplate = getMongoTemplateByMonth(databaseName, collectionName, month);
        MongoTemplateUtil.saveCarrierData(mongoTemplate, collectionName, object, mobile);
    }

    @Override
    public void saveDataByToken(Object object, String databaseName, String collectionName, String month, String token) {
        MongoTemplate mongoTemplate = getMongoTemplateByMonth(databaseName, collectionName, month);
        MongoTemplateUtil.saveCarrierDataByToken(mongoTemplate, collectionName, object, token);
    }

    /**
     * 根据月份动态获取数据库保存数据
     *
     * @param databaseName   数据库名称
     * @param collectionName 数据库表名
     * @param month          数据月份
     * @param entityClass    实体class
     * @return
     */
    @Override
    public <T> T queryDataByPhone(String databaseName, String collectionName, String phone, String month, Class<T> entityClass) {
        MongoTemplate mongoTemplate = getMongoTemplateByMonth(databaseName, collectionName, month);
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("phone").is(phone));
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, entityClass, collectionName);
    }

    @Override
    public <T> T queryDataByToken(String databaseName, String collectionName, String token, String month, Class<T> entityClass) {
        MongoTemplate mongoTemplate = getMongoTemplateByMonth(databaseName, collectionName, month);
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("token").is(token));
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, entityClass, collectionName);
    }

    /**
     * 按手机查询最后一笔数据，用于数据合并
     * 因为除通话短信流量外，都是按照token冗余的
     * 增量任务是新的token，所以没法按照token查询历史数据
     * 只能用手机查询最新数据
     * 由于同一时间同一手机只有一个任务，所以不会有并发问题
     *
     * @param databaseName
     * @param collection
     * @param phone
     * @param month
     * @param entityClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T queryLastDataByPhone(String databaseName, String collection, String phone, String month, Class<T> entityClass) {
        MongoTemplate mongoTemplate = getMongoTemplateByMonth(databaseName, collection, month);
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("phone").is(phone));
        Query query = new Query().addCriteria(criteria).with(new Sort(Sort.Direction.DESC, "updateTime")).limit(1);
        return mongoTemplate.findOne(query, entityClass, collection);
    }

    /**
     * 根据月份动态获取 MongoTemplate
     *
     * @param databaseName
     * @param collectionName
     * @param month
     * @return
     */
    @Override
    public MongoTemplate getMongoTemplateByMonth(String databaseName, String collectionName, String month) {
        month = month.replace("-", "");
        String dataBaseName = databaseName + "_" + month;
        return dynamicMongoCollectionUtil.getDynamicMongoTemplate(dataBaseName, collectionName);
    }

    @Override
    public MongoTemplate getMongoTemplateByMonthWithVersion(String databaseName, String collectionName, String month, String version) {
        month = month.replace("-", "");
        String dataBaseName = databaseName + "_" + month;
        //默认V1
        if (StringUtils.isNotBlank(version) && !"V1".equals(version)) {
            dataBaseName = dataBaseName + "_" + version;
        }
        return dynamicMongoCollectionUtil.getDynamicMongoTemplate(dataBaseName, collectionName);
    }

    /**
     * 获取 通用的 MongoTemplate
     *
     * @param databaseName
     * @param collectionName
     * @return
     */
    @Override
    public MongoTemplate getMongoTemplate(String databaseName, String collectionName) {
        return dynamicMongoCollectionUtil.getDynamicMongoTemplate(databaseName, collectionName);
    }
}

