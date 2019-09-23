package com.xinyan.mongo.utils;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.UpdateOptions;
import com.xinyan.mongo.model.CarrierReportContact;
import com.xinyan.mongo.model.CarrierReportMongoObject;
import com.xinyan.mongo.model.DataMongoObject;
import com.xinyan.mongo.model.ReportMongoObject;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
@Slf4j
public class MongoTemplateUtil {

    /**
     * 保存数据到指定collection，如果token存在则替换；不存在则插入
     *
     * @param mongoTemplate
     * @param collection
     * @param data
     */
    public static void saveData(MongoTemplate mongoTemplate, String collection, DataMongoObject data) {
        saveDataToMongo(mongoTemplate, collection, data, data.getToken());
    }

    /**
     * 保存数据到指定collection，如果token存在则替换；不存在则插入
     *
     * @param mongoTemplate
     * @param collection
     * @param data
     */
    public static void saveCarrierData(MongoTemplate mongoTemplate, String collection, Object data, String mobile) {
        UpdateOptions updateOptions = new UpdateOptions();
        Document document = DataMongoObjectUtil.toDocumentWithoutId(data);
        Object createTime = document.get("createTime");
        Object updateTime = document.get("updateTime");
        if (null != createTime && createTime.getClass().equals(Long.class)) {
            document.put("createTime", new Date(Long.parseLong(createTime.toString())));
        }
        if (null != updateTime && updateTime.getClass().equals(Long.class)) {
            document.put("updateTime", new Date(Long.parseLong(updateTime.toString())));
        }
        mongoTemplate.getCollection(collection).replaceOne(Filters.eq("phone", mobile), document, updateOptions.upsert(true));
    }

    public static void saveCarrierDataByToken(MongoTemplate mongoTemplate, String collection, Object data, String token) {
        UpdateOptions updateOptions = new UpdateOptions();
        Document document = DataMongoObjectUtil.toDocumentWithoutId(data);
        Object createTime = document.get("createTime");
        Object updateTime = document.get("updateTime");
        if (null != createTime && createTime.getClass().equals(Long.class)) {
            document.put("createTime", new Date(Long.parseLong(createTime.toString())));
        }
        if (null != updateTime && updateTime.getClass().equals(Long.class)) {
            document.put("updateTime", new Date(Long.parseLong(updateTime.toString())));
        }
        mongoTemplate.getCollection(collection).replaceOne(Filters.eq("token", token), document, updateOptions.upsert(true));
    }

    /**
     * 保存数据到指定collection，如果token存在则替换；不存在则插入
     *
     * @param mongoTemplate
     * @param collection
     * @param data
     */
    public static void saveReportData(MongoTemplate mongoTemplate, String collection, ReportMongoObject data) {
        saveDataToMongo(mongoTemplate, collection, data, data.getToken());
    }

    public static void saveDataToMongo(MongoTemplate mongoTemplate, String collection, Object data, String token) {
        UpdateOptions updateOptions = new UpdateOptions();
        mongoTemplate.getCollection(collection).replaceOne(Filters.eq("token", token), DataMongoObjectUtil.toDocumentWithoutId(data), updateOptions.upsert(true));
    }

    public static void saveDataToMongoByMenId(MongoTemplate mongoTemplate, String collection, Object data, String memId) {
        UpdateOptions updateOptions = new UpdateOptions();
        mongoTemplate.getCollection(collection).replaceOne(Filters.eq("mem_id", memId), DataMongoObjectUtil.toDocumentWithoutId(data), updateOptions.upsert(true));
    }

    /**
     * 保存数据到指定collection 如果数据超过16M 则保存到gridFS
     * 如果token存在则替换；不存在则插入
     *
     * @param mongoTemplate
     * @param collection
     */
    public static void save(MongoTemplate mongoTemplate, String collection, DataMongoObject dataMongoObject) {
        String token = dataMongoObject.getToken();
        boolean isSixteenMB = ObjectUtil.isSixteenMB(dataMongoObject);
        log.info("验证保存mongo数据大于16M结果:{},token:{}", isSixteenMB, token);
        if (isSixteenMB) {
            saveToGridFS(mongoTemplate, dataMongoObject);
        } else {
            log.info("save,token:{},collection:{}", token, collection);
            saveData(mongoTemplate, collection, dataMongoObject);
        }
    }

    /**
     * 保存数据到指定collection 如果数据超过16M 则保存到gridFS
     * 如果token存在则替换；不存在则插入
     *
     * @param mongoTemplate
     * @param collection
     */
    public static void saveReport(MongoTemplate mongoTemplate, String collection, ReportMongoObject dataMongoObject) {
        String token = dataMongoObject.getToken();
        boolean isSixteenMB = ObjectUtil.isSixteenMB(dataMongoObject);
        log.info("验证保存mongo数据大于16M结果:{},token:{}", isSixteenMB, token);
        if (isSixteenMB) {
            saveReportToGridFS(mongoTemplate, dataMongoObject);
        } else {
            log.info("saveReport,token:{},collection:{}", token, collection);
            saveReportData(mongoTemplate, collection, dataMongoObject);
        }
    }

    /**
     * 返回 DataMongoObject
     *
     * @param mongoTemplate
     * @param collection
     * @param token
     * @return
     */
    public static DataMongoObject query(MongoTemplate mongoTemplate, String collection, String token) {
        FindIterable<Document> documents = mongoTemplate.getCollection(collection).find(Filters.eq("token", token)).limit(1);
        Document document = documents.first();
        if (null == document) {
            log.info("shardingQuery data from gridFS,token:{},collection:{}", token, collection);
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
            GridFSFindIterable filename = gridFSBucket.find(Filters.eq("filename", token));
            GridFSFile first = filename.first();
            if (null == first) {
                log.info("没有查询到数据,数据结果可能为空,db:{},token:{},collection:{}", mongoTemplate.getDb().getName(), token, collection);
                return null;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(first.getObjectId(), outputStream);
            Object obj = ObjectUtil.toObj(outputStream.toByteArray());
            if (null != obj && obj.getClass().equals(DataMongoObject.class)) {
                return (DataMongoObject) obj;
            }
            log.error("查询数据失败,token:{}", token);
            return null;
        }
        return DataMongoObjectUtil.toDataMongoObject(document);
    }

    /**
     * @param mongoTemplate
     * @param collection
     * @param token
     * @return
     */
    public static long count(MongoTemplate mongoTemplate, String collection, String token) {
        return mongoTemplate.getCollection(collection).count(Filters.eq("token", token));
    }

    /**
     * 通过手机号查询是否存在
     *
     * @param mongoTemplate
     * @param collection
     * @param phone
     * @return
     */
    public static long countByPhone(MongoTemplate mongoTemplate, String collection, String phone) {
        return mongoTemplate.getCollection(collection).count(Filters.eq("mobile", phone));
    }

    /**
     * 返回 DataMongoObject
     *
     * @param mongoTemplate
     * @param collection
     * @param token
     * @return
     */
    public static ReportMongoObject queryReport(MongoTemplate mongoTemplate, String collection, String token) {
        FindIterable<Document> documents = mongoTemplate.getCollection(collection).find(Filters.eq("token", token)).limit(1);
        Document document = documents.first();
        if (null == document) {
            log.info("shardingQuery report from gridFS,token:{}", token);
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
            GridFSFindIterable filename = gridFSBucket.find(Filters.eq("filename", token));
            GridFSFile first = filename.first();
            if (null == first) {
                log.error("查询数据为空,token:{}", token);
                return null;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(first.getObjectId(), outputStream);
            Object obj = ObjectUtil.toObj(outputStream.toByteArray());
            if (null != obj && obj.getClass().equals(ReportMongoObject.class)) {
                return (ReportMongoObject) obj;
            }
            log.error("查询数据为空,token:{}", token);
            return null;
        }
        return DataMongoObjectUtil.toMongoObject(document, CarrierReportMongoObject.class);
    }

    /**
     * 返回 报告
     *
     * @param mongoTemplate
     * @param collection
     * @param mobile
     * @return
     */
    public static ReportMongoObject queryReportByPhone(MongoTemplate mongoTemplate, String collection, String mobile) {
        FindIterable<Document> documents = mongoTemplate.getCollection(collection).find(Filters.eq("mobile", mobile)).sort(Sorts.descending("createTime")).limit(1);
        Document document = documents.first();
        return DataMongoObjectUtil.toMongoObject(document, CarrierReportMongoObject.class);
    }

    /**
     * 保存数据到gridFS
     *
     * @param mongoTemplate
     * @param data
     */
    public static void saveToGridFS(MongoTemplate mongoTemplate, DataMongoObject data) {
        saveToGridFS(mongoTemplate, data, data.getToken());
    }

    /**
     * 保存数据到gridFS
     *
     * @param mongoTemplate
     * @param data
     * @param fileName
     */
    public static void saveToGridFS(MongoTemplate mongoTemplate, Object data, String fileName) {
        log.info("save to gridFs , fileName:{}", fileName);
        try {
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
            log.info("删除历史数据,fileName:{}", fileName);
            GridFSFindIterable gridFSFiles = gridFSBucket.find(Filters.eq("filename", fileName));
            gridFSFiles.forEach((Block<? super GridFSFile>) (f) -> {
                gridFSBucket.delete(f.getId());
            });
            log.info("写入新数据,fileName:{}", fileName);
            Long start = System.currentTimeMillis();
            gridFSBucket.uploadFromStream(fileName, ObjectUtil.objectToInputStream(data));
            log.info("保存数据到gridFS中耗时time:{},fillName:{}", System.currentTimeMillis() - start, fileName);
        } catch (Exception e) {
            log.info("save to gridFs failed , fileName:{}" + e.getMessage(), fileName, e);
        }
    }

    /**
     * 保存数据到gridFS
     *
     * @param mongoTemplate
     * @param data
     */
    public static void saveReportToGridFS(MongoTemplate mongoTemplate, ReportMongoObject data) {
        saveToGridFS(mongoTemplate, data, data.getToken());
    }

    public static CarrierReportContact queryContactByToken(MongoTemplate mongoTemplate, String collection, String token) {
        FindIterable<Document> documents = mongoTemplate.getCollection(collection).find(Filters.eq("token", token)).sort(Sorts.descending("createTime")).limit(1);
        Document document = documents.first();
        if (null == document) {
            log.info("queryContactByToken from gridFS,token:{}", token);
            GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
            GridFSFindIterable filename = gridFSBucket.find(Filters.eq("filename", token));
            GridFSFile first = filename.first();
            if (null == first) {
                log.error("查询数据为空,token:{}", token);
                return null;
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            gridFSBucket.downloadToStream(first.getObjectId(), outputStream);
            Object obj = ObjectUtil.toObj(outputStream.toByteArray());
            if (null != obj && obj.getClass().equals(DataMongoObject.class)) {
                return (CarrierReportContact) obj;
            }
            log.error("查询数据为空,token:{}", token);
            return null;
        }
        return DataMongoObjectUtil.toMongoObject(document, CarrierReportContact.class);
    }

    public static void saveContact(MongoTemplate mongoTemplate, String collection, CarrierReportContact contact) {
        String token = contact.getToken();
        boolean isSixteenMB = ObjectUtil.isSixteenMB(contact);
        log.info("saveContact验证保存mongo数据大于16M结果:{},token:{}", isSixteenMB, token);
        if (isSixteenMB) {
            saveToGridFS(mongoTemplate, contact, contact.getToken());
        } else {
            log.info("saveContact,token:{},collection:{}", token, collection);
            saveDataToMongo(mongoTemplate, collection, contact, contact.getToken());
        }
    }

}
