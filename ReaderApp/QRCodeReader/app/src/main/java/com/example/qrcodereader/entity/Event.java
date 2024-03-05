package com.example.qrcodereader.entity;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private Timestamp time;
    private String eventID;
    private String location;
    private String organizer;
    private String name;
    private QRCode qrCode;
    private ArrayList<Map<String, Integer>> attendees;

    public Event(String id, String name, String organizer, String eventLocation, Timestamp eventTime) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
        this.name = name;
        this.organizer = organizer;
        this.qrCode = new QRCode();
        this.attendees =  new ArrayList<Map<String, Integer>>();
    }

    public Event(String id, String name, String organizer, String eventLocation, Timestamp eventTime, QRCode qrCode) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
        this.name = name;
        this.organizer = organizer;
        this.qrCode = qrCode;
        this.attendees =  new ArrayList<Map<String, Integer>>();
    }

    public String getEventID() {
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

    public void setEventID(String eventID) {
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
