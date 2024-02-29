package com.example.qrcodereader.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class User {
    private String userID;
    private String name;

    public User(String deviceID, String userName) {
        this.userID = deviceID;
        this.name = userName;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void createEvent() {
        Event event = new Event(LocalDateTime.now(), 5, "Edmonton", userID);
    }


}
