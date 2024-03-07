package com.example.qrcodereader;


import static org.junit.Assert.assertEquals;


import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import androidx.test.core.app.ApplicationProvider;

import androidx.test.ext.junit.runners.AndroidJUnit4;


import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;


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
        GeoPoint location = new GeoPoint(12.1,12.1);
        Timestamp timeStamp = Timestamp.now();
        adapter.addEvent(eventID, name, organizer, location, timeStamp);

        // Create an Event object with the same details
        Event event = new Event(eventID, name, organizer, location, timeStamp);

        String locationNameExpected = (event.getLocationName() != null) ? event.getLocationName() : "No location";

        // Check that the event was added to the adapter
        assertEquals(1, adapter.getCount());

        // Check that the event details are displayed correctly
        View view = adapter.getView(0, null, new ListView(context));
        assertEquals(name, ((TextView) view.findViewById(R.id.event_text)).getText().toString());
        assertEquals(organizer, ((TextView) view.findViewById(R.id.organizer_text)).getText().toString());
        assertEquals(locationNameExpected, ((TextView) view.findViewById(R.id.event_location_text)).getText().toString());
    }




}
