package com.example.qrcodereader.entity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
// Microsoft Copilot 4/8/2024 "Generate java docs for the following class"
public class User implements Serializable {
    private String userID;
    private String name;
    private String email;
    private String phoneRegion;
    private String phone;
    private Map<String, Long> eventsAttended;
    private GeoPoint location;
    private String ProfilePic;

    /**
     * Constructs a new User object with device ID, user name, and location.
     *
     * @param deviceID The device ID of the user.
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
     * Constructs a new User object with device ID, user name, email, phone region, phone number, and profile picture.
     *
     * @param deviceID    The device ID of the user.
     * @param userName    The name of the user.
     * @param email       The email address of the user.
     * @param phoneRegion The region of the user's phone number.
     * @param phone       The phone number of the user.
     * @param image       The profile picture of the user.
     */
    public User(String deviceID, String userName, String email, String phoneRegion, String phone, String image) {
        this.userID = deviceID;
        this.name = userName;
        this.email = email;
        this.phoneRegion = phoneRegion;
        this.phone = phone;
        this.eventsAttended = new HashMap<String, Long>();
        this.location = null;
        this.ProfilePic = image;
    }
    /**
     * Constructs a new User object with device ID, user name, location, events attended, and profile picture.
     *
     * @param deviceID       The device ID of the user.
     * @param userName       The name of the user.
     * @param location       The location of the user.
     * @param eventsAttended The events attended by the user.
     * @param image          The profile picture of the user.
     */
    public User(String deviceID, String userName, GeoPoint location, Map<String, Long> eventsAttended, String image) {
        this.userID = deviceID;
        this.name = userName;
        this.eventsAttended = eventsAttended;
        this.location = location;
        this.ProfilePic = image;
    }
    /**
     * Constructs a new User object with device ID, user name, location, events attended, profile picture, email, phone region, and phone number.
     *
     * @param deviceID       The device ID of the user.
     * @param userName       The name of the user.
     * @param location       The location of the user.
     * @param eventsAttended The events attended by the user.
     * @param image          The profile picture of the user.
     * @param email          The email address of the user.
     * @param phoneRegion    The region of the user's phone number.
     * @param phone          The phone number of the user.
     */
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
    /**
     * Sets the user ID.
     *
     * @param userID The user ID to set.
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }
    /**
     * Sets the user name.
     *
     * @param name The user name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Sets the user location.
     *
     * @param location The location to set.
     */
    public void setLocation(GeoPoint location) {this.location = location;}
    /**
     * Sets the user profile picture.
     *
     * @param picture The profile picture to set.
     */
    public void setProfilePicture(String picture) {this.ProfilePic = picture;}
    /**
     * Sets the user email address.
     *
     * @param email The email address to set.
     */

    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Sets the user phone number.
     *
     * @param phone The phone number to set.
     */

    public void setPhone(String phone) {
        this.phone = phone;
    }
    /**
     * Sets the user phone region.
     *
     * @param phoneRegion The phone region to set.
     */
    public void setPhoneRegion(String phoneRegion) {
        this.phoneRegion = phoneRegion;
    }
    /**
     * Creates a new event for the user.
     *
     * @param id       The ID of the event.
     * @param name     The name of the event.
     * @param location The location of the event.
     * @param time     The time of the event.
     */
    public void createEvent(String id, String name, GeoPoint location, Timestamp time) {
    }
    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    public String getUserID() {return userID;}
    /**
     * Gets the user name.
     *
     * @return The user name.
     */
    public String getName() {return name;}
    /**
     * Gets the user email address.
     *
     * @return The email address.
     */

    public String getEmail() {
        return email;
    }
    /**
     * Gets the user phone number.
     *
     * @return The phone number.
     */

    public String getPhone() {
        return phone;
    }
    /**
     * Gets the user phone region.
     *
     * @return The phone region.
     */

    public String getPhoneRegion() {
        return phoneRegion;
    }
    /**
     * Gets the user location.
     *
     * @return The user location.
     */

    public GeoPoint getLocation(){
        return this.location;
    }

    /**
     * Gets the user profile picture.
     *
     * @return The profile picture.
     */
    public String getProfilePicture() {return ProfilePic;}
    /**
     * Gets the events attended by the user.
     *
     * @return The events attended.
     */
    public Map<String, Long> getEventsAttended() {
        return eventsAttended;
    }
}
