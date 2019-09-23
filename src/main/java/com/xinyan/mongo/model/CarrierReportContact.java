package com.xinyan.mongo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
@Document(collection = "carrier_report_contact")
public class CarrierReportContact implements Serializable {
    @Id
    private String id;
    private String updateTime;
    @Indexed(
            background = true
    )
    private String token;
    private Map<String, String> contacts;
    private Map<String, String> emergencyContacts;

    public String getId() {
        return this.id;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public String getToken() {
        return this.token;
    }

    public Map<String, String> getContacts() {
        return this.contacts;
    }

    public Map<String, String> getEmergencyContacts() {
        return this.emergencyContacts;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setContacts(Map<String, String> contacts) {
        this.contacts = contacts;
    }

    public void setEmergencyContacts(Map<String, String> emergencyContacts) {
        this.emergencyContacts = emergencyContacts;
    }

    public CarrierReportContact() {
    }

    public String toString() {
        return "CarrierReportContact(id=" + this.getId() + ", updateTime=" + this.getUpdateTime() + ", token=" + this.getToken() + ", contacts=" + this.getContacts() + ", emergencyContacts=" + this.getEmergencyContacts() + ")";
    }
}

