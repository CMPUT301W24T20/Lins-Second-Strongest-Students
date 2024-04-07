package com.example.qrcodereader;

import android.content.Context;
import android.widget.ListView;
import android.widget.TextView;

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
import static org.junit.Assert.assertNotNull;

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
        // Create a temporary ListView
        ListView listView = new ListView(context);

        // Create a new event and add it to the adapter
        String eventID = "1";
        String name = "Test Event";
        String organizer = "Test Organizer";
        GeoPoint location = new GeoPoint(12.1, 12.1);
        String locationName = "Test Location";
        Timestamp time = Timestamp.now();
        String poster = "poster_url";

        Event event = new Event(eventID, name, organizer, location, time, poster);
        events.add(event);

        // Check that the event was added to the adapter
        assertEquals(1, adapter.getCount());

        // Check that the event details are displayed correctly
        assertNotNull(adapter.getView(0, null, listView));
        TextView eventNameTextView = adapter.getView(0, null, listView).findViewById(R.id.event_text);
        TextView organizerTextView = adapter.getView(0, null, listView).findViewById(R.id.organizer_text);
        TextView locationNameTextView = adapter.getView(0, null, listView).findViewById(R.id.event_location_text);

        assertNotNull(eventNameTextView);
        assertNotNull(organizerTextView);
        assertNotNull(locationNameTextView);

        assertEquals(name, eventNameTextView.getText().toString());
        assertEquals(organizer, organizerTextView.getText().toString());
        assertEquals(locationName, locationNameTextView.getText().toString());
        assertEquals(poster, event.getPoster());
    }
}
