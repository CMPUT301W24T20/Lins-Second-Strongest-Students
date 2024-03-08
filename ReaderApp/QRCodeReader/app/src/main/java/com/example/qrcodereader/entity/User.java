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
 * Represents a user entity.
 */
public class User implements Serializable {
    private String userID;
    private String name;
    private Map<String, Long> eventsAttended;
    private GeoPoint location;


    private String DefaultProfile;

    /**
     * Constructs a new user object.
     * @param deviceID The unique identifier of the user.
     * @param userName The name of the user.
     * @param location The location of the user.
     */
    public User(String deviceID, String userName, GeoPoint location) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = new HashMap<String, Long>();
        this.location = location;

    }

    /**
     * Constructs a new user object with events attended.
     * @param deviceID The unique identifier of the user.
     * @param userName The name of the user.
     * @param location The location of the user.
     * @param eventsAttended The events attended by the user.
     */
    public User(String deviceID, String userName, GeoPoint location, Map<String, Long> eventsAttended) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = eventsAttended;
        this.location = location;

    }

    /**
     * Constructs a new user object with events attended.
     * @param deviceID The unique identifier of the user.
     * @param userName The name of the user.
     * @param eventsAttended The events attended by the user.
     */
    public User(String deviceID, String userName, Map<String, Long> eventsAttended) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = eventsAttended;
    }

    /**
     * Sets the user's ID.
     * @param userID The user's ID.
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Sets the user's name.
     * @param name The user's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's location.
     * @return The user's location.
     */
    public void setLocation(GeoPoint location) {this.location = location;}
    public GeoPoint getLocation(){
        return this.location;
    }

    /**
     * Gets the user's profile picture.
     * @return The user's profile picture.
     */
    public String getProfilePicture() {
        return DefaultProfile;
    }

    /**
     * Sets the user's profile picture.
     * @param defaultProfile The user's profile picture.
     */
    public void setProfilePicture(String defaultProfile) {
        DefaultProfile = defaultProfile;
    }

    /**
     * Creates a new event for the user.
     * @param id The ID of the event.
     * @param name The name of the event.
     * @param location The location of the event.
     * @param time The time of the event.
     */
    public void createEvent(String id, String name, GeoPoint location, Timestamp time) {
    }

    /**
     * Gets the user's ID.
     * @return The user's ID.
     */
    public String getUserID() {return userID;}

    /**
     * Gets the user's name.
     * @return The user's name.
     */
    public String getName() {return name;}
}
