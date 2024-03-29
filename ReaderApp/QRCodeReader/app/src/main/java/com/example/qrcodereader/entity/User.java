package com.example.qrcodereader.entity;

import android.location.Location;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

/**
 *  Represent the user of the app
 *  <p>
 *      Store details of the user
 *  </p>
 *  <p>
 *      Matches the user documents in users collection
 *  </p>
 *  @author Son and Duy
 */
public class User implements Serializable {
    private String userID;
    private String name;

    private String contact;
    private Map<String, Long> eventsAttended;
    private GeoPoint location;
    private String ProfilePic;


    public User(String deviceID, String userName, GeoPoint location) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = new HashMap<String, Long>();
        this.location = location;

    }

    public User(String deviceID, String userName, GeoPoint location, Map<String, Long> eventsAttended, String image) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = eventsAttended;
        this.location = location;
        this.ProfilePic = image;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setContact(String contact) {this.contact = contact;}
    public void setLocation(GeoPoint location) {this.location = location;}
    public void setProfilePicture(String picture) {this.ProfilePic = picture;}
    public void createEvent(String id, String name, GeoPoint location, Timestamp time) {
    }

    public String getUserID() {return userID;}
    public String getName() {return name;}
    public String getContact() {return contact;}
    public GeoPoint getLocation(){
        return this.location;
    }
    public String getProfilePicture() {return ProfilePic;}
}
