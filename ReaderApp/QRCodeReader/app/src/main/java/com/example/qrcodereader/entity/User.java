package com.example.qrcodereader.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import com.google.firebase.Timestamp;

public class User implements Serializable {
    private String userID;
    private String name;
    private ArrayList<Map<String, Integer>> events;

    public User(String deviceID, String userName) {
        this.userID = deviceID;
        this.name = userName;
        this.events = new ArrayList<Map<String, Integer>>();
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
