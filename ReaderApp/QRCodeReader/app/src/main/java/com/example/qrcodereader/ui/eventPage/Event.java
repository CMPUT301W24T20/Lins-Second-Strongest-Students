package com.example.qrcodereader.ui.eventPage;
import java.util.Random;

public class Event {
    private int eventID;
    private String eventName;
    private String eventLocation;
    private String eventDate;

    public Event(String eventName, String eventLocation, String eventDate) {
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.eventID = new Random().nextInt(1000);
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventDate() {
        return eventDate;
    }

}
