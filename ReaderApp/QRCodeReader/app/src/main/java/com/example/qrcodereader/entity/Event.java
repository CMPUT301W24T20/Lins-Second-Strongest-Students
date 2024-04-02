package com.example.qrcodereader.entity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *  Represent the event
 *  <p>
 *      Store all relevant event details
 *  </p>
 *  <p>
 *      Matches the event documents in events collection
 *  </p>
 *  @author Son and Duy
 */
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
    private String poster;

    /**
     * Constructor for the Event class
     * @param id The event's unique identifier
     * @param name The name of the event
     * @param organizer The name of the organizer
     * @param eventLocation The location of the event
     * @param eventTime The time of the event
     */
    public Event(String id, String name, String organizer, GeoPoint eventLocation, Timestamp eventTime, String EPoster) {
        this.time = eventTime;
        this.eventID = id;
        this.location = eventLocation;
        this.name = name;
        this.organizer = organizer;
        this.qrCode = new QRCode();
        this.attendees =  new HashMap<String, Long>();
        this.poster = EPoster;
    }

    /**
     * Constructor for the Event class
     * @param id The event's unique identifier
     * @param name The name of the event
     * @param location The location of the event
     * @param locationName The time of the event
     * @param time The time of the event
     * @param organizer The name of the organizer
     * @param organizerID The unique identifier of the organizer
     * @param qrCode The QR code for the event
     * @param attendeeLimit The maximum number of attendees
     * @param attendees The map of attendees
     */
    public Event(String id, String name, GeoPoint location, String locationName, Timestamp time, String organizer, String organizerID, QRCode qrCode, int attendeeLimit, Map<String, Long> attendees, String EPoster) {
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
        this.poster = EPoster;
    }

    /**
     * Get the unique identifier of the event
     * @return The unique identifier of the event
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Get the name of the event
     * @return The name of the event
     */
    public String getEventName() {
        return name;
    }

    /**
     * Get the location of the event
     * @return The location of the event
     */
    public GeoPoint getLocation() {
        return location;
    }

    /**
     * Get the name of the location of the event
     * @return The name of the location of the event
     */
    public String getLocationName() {
        if (locationName == null) {
            return String.format(Locale.getDefault(), "%f, %f",
                    location.getLatitude(),
                    location.getLongitude());
        }
        return locationName;
    }

    /**
     * Get the time of the event
     * @return The time of the event
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * Get the name of the organizer
     * @return The name of the organizer
     */
    public String getOrganizer() {
        return organizer;
    }

    /**
     * Get the unique identifier of the organizer
     * @return The unique identifier of the organizer
     */
    public String getOrganizerID() {
        return organizerID;
    }

    /**
     * Get the QR code for the event
     * @return The QR code for the event
     */
    public QRCode getQrCode() {
        return qrCode;
    }

    /**
     * Get the maximum number of attendees
     * @return The maximum number of attendees
     */
    public int getAttendeeLimit() {
        return attendeeLimit;
    }

    /**
     * Get the map of attendees
     * @return The map of attendees
     */
    public Map<String, Long> getAttendees() {
        return attendees;
    }

    /**
     * Set the unique identifier of the event
     * @param eventID The unique identifier of the event
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Set the location of the event
     * @param location The location of the event
     */
    public void setLocation(GeoPoint location) {
        this.location = location;
    }


    /**
     * Set the name of the organizer of the event
     * @param organizer The name of the organizer of the event
     */
    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getPoster() {return this.poster;}

    /**
     * Set the time of the event
     * @param time The time of the event
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * Set the name for the event
     * @param name The name of the event
     */
    public void setEventName(String name) {
        this.name = name;
    }

    /**
     * Check if the event is full
     * @return True if the event is full, false otherwise
     */
    public boolean isFull() {
        // -1 means no limit, so the event is never full
        // else, the number of attendees is compared to the limit
        if (attendeeLimit == -1) {
            return false;
        }
        else return attendees.size() >= attendeeLimit;
    }
}
