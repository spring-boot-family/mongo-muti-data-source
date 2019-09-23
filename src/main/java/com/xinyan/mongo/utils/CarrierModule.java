package com.xinyan.mongo.utils;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
public enum CarrierModule {
    BASIC("BASIC", "基本信息"),
    CALL("CALL", "通话详单"),
    SMS("SMS", "短信记录"),
    NET("NET", "上网记录"),
    BILL("BILL", "账单信息"),
    PACKAGE("PACKAGE", "套餐信息"),
    FAMILY("FAMILY", "亲情号码"),
    RECHARGE("RECHARGE", "充值记录");

    private String name;
    private String desc;

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    private CarrierModule(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
