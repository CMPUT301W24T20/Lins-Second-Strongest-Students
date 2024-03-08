package com.example.qrcodereader.entity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Event {
    private String eventID;
    private String name;
    private GeoPoint location;
    private String locationName;
    private Timestamp time;
    private String organizer;
    private String organizerID;
    private QRCode qrCode;
    private int attendeeLimit; // -1: no limit, else limit is the number that the organizer entered
    private Map<String, Long> attendees;


    public Event(String id, String name, String organizer, GeoPoint eventLocation, Timestamp eventTime) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
        this.name = name;
        this.organizer = organizer;
        this.qrCode = new QRCode();
        this.attendees =  new HashMap<String, Long>();
    }

    public Event(String id, String name, String organizer, GeoPoint eventLocation, String locationName, Timestamp eventTime) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
        this.locationName = locationName;
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

    public Event(String id, String name, GeoPoint location, String locationName, Timestamp time, String organizer, String organizerID, QRCode qrCode,Map<String, Long> attendees) {
        this.eventID = id;
        this.name = name;
        this.location = location;
        this.locationName = locationName;
        this.time = time;
        this.organizer = organizer;
        this.organizerID = organizerID;
        this.qrCode = qrCode;
        this.attendeeLimit = -1;
        this.attendees = attendees;
    }

    public Event(String id, String name, GeoPoint location, String locationName, Timestamp time, String organizer, String organizerID, QRCode qrCode, int attendeeLimit, Map<String, Long> attendees) {
        this.eventID = id;
        this.name = name;
        this.location = location;
        this.locationName = locationName;
        this.time = time;
        this.organizer = organizer;
        this.organizerID = organizerID;
        this.qrCode = qrCode;
        this.attendeeLimit = attendeeLimit;
        this.attendees = attendees;
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
    public String getOrganizerID() {
        return organizerID;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getEventName() {
        return name;
    }

    public Map<String, Long> getAttendees() {
        return attendees;
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
    public int getAttendeeLimit() {
        return attendeeLimit;
    }

    public boolean isFull() {
        // -1 means no limit, so the event is never full
        // else, the number of attendees is compared to the limit
        if (attendeeLimit == -1) {
            return false;
        }
        else return attendees.size() >= attendeeLimit;
    }

    public String getLocationName() {
        if (locationName == null) {
            return String.format(Locale.getDefault(), "%f, %f",
                    location.getLatitude(),
                    location.getLongitude());
        }
        return locationName;
    }

    public QRCode getQrCode() {
        return qrCode;
    }
}
