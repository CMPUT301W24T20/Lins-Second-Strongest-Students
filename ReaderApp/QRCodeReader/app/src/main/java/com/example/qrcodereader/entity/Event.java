package com.example.qrcodereader.entity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private Timestamp time;
    private String eventID;
    private GeoPoint location;
    private String organizer;
    private String name;
    private QRCode qrCode;
    private Map<String, Long> attendees;
    private String locationName;

    public Event(String id, String name, String organizer, GeoPoint eventLocation, Timestamp eventTime) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
        this.name = name;
        this.organizer = organizer;
        this.qrCode = new QRCode();
        this.attendees =  new HashMap<String, Long>();
    }

    public Event(String id, String name, String organizer, GeoPoint eventLocation, Timestamp eventTime, QRCode qrCode) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
        this.name = name;
        this.organizer = organizer;
        this.qrCode = qrCode;
        this.attendees =  new HashMap<String, Long>();
    }

    public Event(String id, String name, String organizer, GeoPoint eventLocation, Timestamp eventTime, String locationName) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
        this.name = name;
        this.organizer = organizer;
        this.locationName = locationName;
    }

    public String getEventID() {
        return eventID;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getEventName() {
        return name;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setEventName(String name) {
        this.name = name;
    }

    public void addAttendee() {}

    public String getLocationName() {
        if (locationName == null) {
            return location.toString();
        }
        return locationName;
    }
}
