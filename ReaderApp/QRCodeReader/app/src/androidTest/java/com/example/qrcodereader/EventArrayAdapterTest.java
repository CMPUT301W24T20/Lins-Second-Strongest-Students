package com.example.qrcodereader;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class EventArrayAdapterTest {

    private EventArrayAdapter adapter;
    private ArrayList<Event> events;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        events = new ArrayList<>();
        adapter = new EventArrayAdapter(context, events);
    }

    @Test
    public void testAddEvent() {
        // Create a new event and add it to the adapter
        String eventID = "1";
        String name = "Test Event";
        String organizer = "Test Organizer";
        GeoPoint location = new GeoPoint(12.1, 12.1);
        String locationName = "Test Location";
        Timestamp time = Timestamp.now();
        String poster = "poster_url";

        Event event = new Event(eventID, name, location, locationName, time, organizer, "organizerID", null, -1, null, poster);
        adapter.addEvent(eventID, name, location, locationName, time, organizer, "organizerID", null, -1, null, poster);

        // Check that the event was added to the adapter
        assertEquals(1, adapter.getCount());

        // Get the event from the adapter and check its details
        Event addedEvent = adapter.getItem(0);
        assertEquals(eventID, addedEvent.getEventID());
        assertEquals(name, addedEvent.getEventName());
        assertEquals(organizer, addedEvent.getOrganizer());
        assertEquals(location, addedEvent.getLocation());
        assertEquals(locationName, addedEvent.getLocationName());
        assertEquals(time, addedEvent.getTime());
        assertEquals(poster, addedEvent.getPoster());
    }
}
