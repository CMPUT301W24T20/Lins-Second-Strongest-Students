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
    private String email;
    private String phoneRegion;
    private String phone;
    private Map<String, Long> eventsAttended;
    private GeoPoint location;
    private String ProfilePic;


    public User(String deviceID, String userName, GeoPoint location) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = new HashMap<String, Long>();
        this.location = location;

    }

    public User(String deviceID, String userName, String email, String phoneRegion, String phone) {
        this.userID = deviceID;
        this.name = userName;
        this.email = email;
        this.phoneRegion = phoneRegion;
        this.phone = phone;
        this.eventsAttended = new HashMap<String, Long>();
        this.location = null;
        this.ProfilePic = null;
    }

    public User(String deviceID, String userName, GeoPoint location, Map<String, Long> eventsAttended, String image) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = eventsAttended;
        this.location = location;
        this.ProfilePic = image;
    }

    public User(String deviceID, String userName, GeoPoint location, Map<String, Long> eventsAttended, String image, String email, String phoneRegion, String phone) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = eventsAttended;
        this.location = location;
        this.ProfilePic = image;
        this.email = email;
        this.phoneRegion = phoneRegion;
        this.phone = phone;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setLocation(GeoPoint location) {this.location = location;}
    public void setProfilePicture(String picture) {this.ProfilePic = picture;}

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhoneRegion(String phoneRegion) {
        this.phoneRegion = phoneRegion;
    }

    public void createEvent(String id, String name, GeoPoint location, Timestamp time) {
    }

    public String getUserID() {return userID;}
    public String getName() {return name;}

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoneRegion() {
        return phoneRegion;
    }

    public GeoPoint getLocation(){
        return this.location;
    }
    public String getProfilePicture() {return ProfilePic;}
    public Map<String, Long> getEventsAttended() {
        return eventsAttended;
    }
}
