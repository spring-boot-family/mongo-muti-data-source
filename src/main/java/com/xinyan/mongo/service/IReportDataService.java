package com.xinyan.mongo.service;

import com.xinyan.mongo.model.CarrierReportContact;
import com.xinyan.mongo.model.ReportMongoObject;

/**
 * 报告服务
 *
 * @author weimin_ruan
 * @date 2019/9/23
 */
public interface IReportDataService {

    /**
     * 查询
     *
     * @param phone
     * @return
     */
    ReportMongoObject queryCarrierReportWithPhone(String phone);

    /**
     * 查询
     *
     * @param token
     * @param version
     * @return
     */
    ReportMongoObject query(String token, String version);

    /**
     * 保存
     *
     * @param reportMongoObject
     * @param version
     */
    void save(ReportMongoObject reportMongoObject, String version);

    /**
     * 是否存在
     *
     * @param token
     * @return boolean
     */
    boolean exists(String token);

    /**
     * 查询通讯录和紧急联系人
     *
     * @param token
     * @return
     */
    CarrierReportContact queryContactByToken(String token);

    /**
     * 保存通讯录和紧急联系人
     *
     * @param contact
     */
    void saveContact(CarrierReportContact contact);

}

