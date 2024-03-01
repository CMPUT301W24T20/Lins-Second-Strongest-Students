package com.example.qrcodereader.entity;

import java.time.LocalDateTime;

public class Event {
    private LocalDateTime time;
    private int eventID;
    private String location;
    private String organizer;
    private String name;
    public Event(LocalDateTime eventTime, int id, String eventLocation, String organizer) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
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

    public LocalDateTime getTime() {
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

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setEventName(String name) {
        this.name = name;
    }
}
