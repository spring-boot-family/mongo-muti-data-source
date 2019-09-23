package com.xinyan.mongo.model;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
public class CarrierReportMongoObject extends ReportMongoObject {
    private String mobile;

    public CarrierReportMongoObject() {
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
