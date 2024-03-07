package com.example.qrcodereader.entity;

import android.graphics.Bitmap;
import android.location.Location;
import android.widget.ImageView;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class User implements Serializable {
    private String userID;
    private String name;
    private Map<String, Long> eventsAttended;
    private String defaultPicture;
    private Bitmap personalizedPicture;

    private Location location;

    public User(String deviceID, String userName, Location location) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = new HashMap<String, Long>();
        this.location = location;
    }

//    public User(String deviceID, String userName, Location location Map<String, Long> eventsAttended) {
//        this.userID = deviceID;
//        this.name = userName;
//        this.events = eventsAttended;
//        this.location = location;
//    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {this.location = location;}
    public Location getLocation(){
        return this.location;
    }
    public void setProfilePicture(String profilePictureURL) {
        this.defaultPicture = profilePictureURL;
    }

    public void createEvent(String id, String name, GeoPoint location, Timestamp time) {
    }

    public String getUserID() {return userID;}
    public String getName() {return name;}
    public String getProfilePicture() {return defaultPicture;}


}
