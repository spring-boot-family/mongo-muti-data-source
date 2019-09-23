package com.xinyan.mongo.service;

import com.xinyan.mongo.model.CarrierReportContact;
import com.xinyan.mongo.model.ReportMongoObject;
import com.xinyan.mongo.utils.MongoTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
@Slf4j
@Service
public class ReportDataServiceImpl implements IReportDataService {

    //@Value("${mongo.collection.report.default}")
    private String defaultCollectionName;
    //@Value("${mongo.database.report.carrier}")
    private String carrierDatabaseName;
    //@Value("${mongo.database.report.report-carrier-contact}")
    private String contactDatabaseName;
    //@Value("${mongo.collection.report.emergContactsV2}")
    private String carrierContactCollectionNameV2;

    @Autowired
    private IDynamicDataBaseService dynamicDataBaseServiceImpl;

    /**
     * 用手机号查，取不到任务时间，所以查询当月和上月的最新一笔
     *
     * @param phone
     * @return
     */
    @Override
    public ReportMongoObject queryCarrierReportWithPhone(String phone) {
        final String db = carrierDatabaseName;
        final String col = defaultCollectionName;
        final LocalDateTime now = LocalDateTime.now();
        final String thisMonth = String.format("%1$tY%1$tm", now);
        MongoTemplate mongoTemplate = dynamicDataBaseServiceImpl.getMongoTemplateByMonth(db, col, thisMonth);
        ReportMongoObject mongoObject = MongoTemplateUtil.queryReportByPhone(mongoTemplate, col, phone);
        if (mongoObject == null) {
            final String lastMonth = String.format("%1$tY%1$tm", now.minus(1, ChronoUnit.MONTHS));
            log.info("查不到数据查上个月,phone:{},{}->{}", phone, thisMonth, lastMonth);
            mongoTemplate = dynamicDataBaseServiceImpl.getMongoTemplateByMonth(db, col, lastMonth);
            mongoObject = MongoTemplateUtil.queryReportByPhone(mongoTemplate, col, phone);
        }
        return mongoObject;
    }

    @Override
    public ReportMongoObject query(String token, String version) {
        final String createTime = ""; // params.getCreateTime();
        final String month = "CreateTime To Date, " + "Date To Month";
        MongoTemplate carrierMongoTemplate = dynamicDataBaseServiceImpl.getMongoTemplateByMonthWithVersion(carrierDatabaseName,
                defaultCollectionName, month, version);
        return MongoTemplateUtil.queryReport(carrierMongoTemplate, defaultCollectionName, token);
    }

    @Override
    public void save(ReportMongoObject reportMongoObject, String version) {
        final String createTime = ""; // params.getCreateTime();
        final String month = "CreateTime To Date, " + "Date To Month";
        MongoTemplate carrierMongoTemplate = dynamicDataBaseServiceImpl.getMongoTemplateByMonthWithVersion(carrierDatabaseName,
                defaultCollectionName, month, version);
        MongoTemplateUtil.saveReport(carrierMongoTemplate, defaultCollectionName, reportMongoObject);
    }

    @Override
    public boolean exists(String token) {
        final String createTime = ""; // params.getCreateTime();
        final String month = "CreateTime To Date, " + "Date To Month";
        MongoTemplate carrierMongoTemplate = dynamicDataBaseServiceImpl.getMongoTemplateByMonth(carrierDatabaseName,
                defaultCollectionName, month);
        return MongoTemplateUtil.count(carrierMongoTemplate, defaultCollectionName, token) > 0;
    }

    @Override
    public CarrierReportContact queryContactByToken(String token) {
        MongoTemplate mongoTemplate = dynamicDataBaseServiceImpl.getMongoTemplate(contactDatabaseName, contactDatabaseName);
        return MongoTemplateUtil.queryContactByToken(mongoTemplate, contactDatabaseName, token);
    }

    @Override
    public void saveContact(CarrierReportContact contact) {
        MongoTemplate mongoTemplate = dynamicDataBaseServiceImpl.getMongoTemplate(contactDatabaseName, contactDatabaseName);
        MongoTemplateUtil.saveContact(mongoTemplate, contactDatabaseName, contact);
    }

}
