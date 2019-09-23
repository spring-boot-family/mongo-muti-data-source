package com.xinyan.mongo.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
@Document(collection = "data")
public class DataMongoObject implements Serializable, Cloneable {
    private static final Logger log = LoggerFactory.getLogger(DataMongoObject.class);
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    private String token;
    private String sourceName;
    private String sourceDesc;
    private String updateTime;
    private String dataType = "json";
    private Object data;

    public DataMongoObject() {
    }

    public DataMongoObject clone() {
        try {
            return (DataMongoObject) super.clone();
        } catch (CloneNotSupportedException var3) {
            log.error(var3.getMessage(), var3);
            DataMongoObject mongoObject = new DataMongoObject();
            mongoObject.setData(this.data);
            mongoObject.setSourceName(this.sourceName);
            mongoObject.setSourceDesc(this.sourceDesc);
            mongoObject.setUpdateTime(this.updateTime);
            mongoObject.setId(this.id);
            mongoObject.setToken(this.token);
            mongoObject.setDataType(this.dataType);
            return mongoObject;
        }
    }

    public String getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public String getSourceDesc() {
        return this.sourceDesc;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public String getDataType() {
        return this.dataType;
    }

    public Object getData() {
        return this.data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setSourceDesc(String sourceDesc) {
        this.sourceDesc = sourceDesc;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

