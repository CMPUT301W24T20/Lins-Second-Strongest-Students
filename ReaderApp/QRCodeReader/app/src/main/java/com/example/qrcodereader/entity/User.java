package com.example.qrcodereader.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import com.google.firebase.Timestamp;

public class User {
    private String userID;
    private String name;

    private Boolean access;

    public User(String deviceID, String userName, Boolean LocationAcess) {
        this.userID = deviceID;
        this.name = userName;
        this.access = LocationAcess;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccess(Boolean access) {
        this.access = access;
    }

    public void createEvent(int id, String name, String location, Timestamp time) {
        Event event = new Event(id, name, userID, location, time);
    }

    public String getUserID() {return userID;}
    public String getName() {return name;}
    public Boolean getAccess() {return access;}

}
