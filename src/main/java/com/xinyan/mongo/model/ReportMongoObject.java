package com.xinyan.mongo.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
public class ReportMongoObject implements Serializable {
    @Id
    protected String id;
    protected String token;
    protected String siteName;
    protected Object data;
    protected String status;
    protected String createTime;
    protected String type;

    public ReportMongoObject() {
    }

    public String getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }

    public String getSiteName() {
        return this.siteName;
    }

    public Object getData() {
        return this.data;
    }

    public String getStatus() {
        return this.status;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public String getType() {
        return this.type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setType(String type) {
        this.type = type;
    }
}
