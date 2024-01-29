package com.example.ufaapp.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String username;
    private String idNumber;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;

    public UserModel() {
    }

    public UserModel(String phone, String username,String idNumber, Timestamp createdTimestamp,String userId) {
        this.phone = phone;
        this.username = username;
        this.idNumber = idNumber;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }
    public String getIdNumber() {
        return idNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
