package com.example.qrcodereader.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.Timestamp;

public class User implements Serializable {
    private String userID;
    private String name;
    private Map<String, Long> eventsAttended;

    public User(String deviceID, String userName) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = new HashMap<String, Long>();
    }

    public User(String deviceID, String userName, Map<String, Long> eventsAttended) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = eventsAttended;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void createEvent(String id, String name, String location, Timestamp time) {
        Event event = new Event(id, name, userID, location, time);
    }

    public String getUserID() {return userID;}
    public String getName() {return name;}

}
