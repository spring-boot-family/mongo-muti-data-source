package com.xinyan.mongo.common;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
public enum StatusCode {
    PARAMS_EMPTY("10001", "缺少必传参数"),
    PARAMS_UNKNOWN("10002", "参数错误"),
    WEBSITE_ERROR("10003", "暂不支持此网站"),
    WEBSITE_TIMEOUT("10004", "网站超时"),
    SERVER_ERROR("20001", "服务器错误"),
    THREAD_POOL_NEARLY_FULL("20002", "线程池任务即将满了"),
    THREAD_POOL_IS_FULL("20003", "线程池任务已满"),
    DB_ERROR("30001", "数据库错误"),
    UNKNOWN("99999", "未知错误");

    private String code;
    private String desc;

    private StatusCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
