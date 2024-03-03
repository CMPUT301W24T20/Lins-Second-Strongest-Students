package com.example.qrcodereader.entity;

import com.google.firebase.Timestamp;

public class Event {
    private Timestamp time;
    private int eventID;
    private String location;
    private String organizer;
    private String name;
    public Event(int id, String name, String organizer, String eventLocation, Timestamp eventTime) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
        this.name = name;
        this.organizer = organizer;
    }

    public int getEventID() {
        return eventID;
    }

    public String getLocation() {
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

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public void setLocation(String location) {
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
}
