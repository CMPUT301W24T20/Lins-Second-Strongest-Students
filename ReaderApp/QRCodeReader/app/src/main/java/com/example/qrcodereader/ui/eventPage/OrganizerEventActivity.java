package com.example.qrcodereader.ui.eventPage;
import static android.content.ContentValues.TAG;

import com.example.qrcodereader.MapViewOrganizer;
import com.example.qrcodereader.NavBar;
import com.example.qrcodereader.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;
import com.example.qrcodereader.entity.QRCode;
import com.example.qrcodereader.util.AppDataHolder;
import com.example.qrcodereader.util.EventFetcher;

import com.example.qrcodereader.util.LocalEventsStorage;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  Activity for users to view all events they have created.
 *  <p>
 *      User can press on an event to bring them to EventDetailsOrganizerActivity
 *  </p>
 *  @author Son and Duy and Khushdeep
 */
public class OrganizerEventActivity extends NavBar {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference pastEventsRef;
    private String userid;
    private String username;
    private ListView eventList;
    private EventArrayAdapter eventArrayAdapter;
    private ArrayList<Event> eventDataList;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());


    /**
     * This method is called when the activity is starting.
     * It initializes the activity and sets up the Firestore references and ListView.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.organizer_events);
        TextView title = findViewById(R.id.upcoming_events);
        title.setText(R.string.OrgTitle);

        setupTextViewButton(R.id.home_button);
        setupTextViewButton(R.id.event_button);
        setupTextViewButton(R.id.scanner_button);
        setupTextViewButton(R.id.notification_button);
        setupTextViewButton(R.id.bottom_profile_icon);
        //getSupportActionBar().hide();


        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        pastEventsRef = db.collection("pastEvents");

        eventList = findViewById(R.id.event_list_attendee);
        eventDataList = new ArrayList<>();

        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        fetchLocal(this);
        setupRealTimeEventUpdates();

        TextView createEventButton = findViewById(R.id.browse_button);
        createEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventActivity.this, CreateEventActivity.class);
            intent.putExtra("userid", userid);
            intent.putExtra("username", username);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = eventDataList.get(position);

                Intent detailIntent = new Intent(OrganizerEventActivity.this, EventDetailsOrganizerActivity.class);
                detailIntent.putExtra("eventID", selectedEvent.getEventID());
                startActivity(detailIntent);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.attendee_events;
    }

    public void fetchEvents(Context context) {
        ArrayList<Event> tempEventDataList = AppDataHolder.getInstance().getOrganizerEvents(context);
        if(tempEventDataList != null) {
            eventDataList = tempEventDataList;
            eventDataList.clear();

            if (eventDataList.size() >= 2) {
                Collections.sort(eventDataList, new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        return e1.getTime().compareTo(e2.getTime()); // Ascending
                    }
                });
            }

            for (Event event : eventDataList) {
                eventArrayAdapter.addEvent(event.getEventID(), event.getEventName(), event.getLocation(), event.getLocationName(), event.getTime(), event.getOrganizer(), event.getOrganizerID(), event.getQrCode(), event.getAttendeeLimit(), event.getAttendees(), event.getPoster());
            }

            eventArrayAdapter.notifyDataSetChanged();
        }
    }

    public void fetchOrganizerEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("events")
                .whereEqualTo("organizerID", deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            executorService.execute(() -> {
                                ArrayList<Event> events = new ArrayList<>();
                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    String id = documentSnapshot.getId();
                                    String name = documentSnapshot.getString("name");
                                    GeoPoint location = documentSnapshot.getGeoPoint("location");
                                    String locationName = documentSnapshot.getString("locationName");
                                    Timestamp time = documentSnapshot.getTimestamp("time");
                                    String organizer = documentSnapshot.getString("organizer");
                                    String organizerID = documentSnapshot.getString("organizerID");
                                    QRCode qrCode = new QRCode(documentSnapshot.getString("qrCode"));
                                    int attendeeLimit = documentSnapshot.getLong("attendeeLimit").intValue();
                                    Map<String, Long> attendees = (Map<String, Long>) documentSnapshot.get("attendees");
                                    String EPoster = documentSnapshot.getString("EPoster");

                                    Event event = new Event(id, name, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit, attendees, EPoster);

                                    events.add(event);
                                }
                                LocalEventsStorage.saveEvents(this, events, "organizerEvents.json");
                                AppDataHolder.getInstance().loadOrganizerEvents(this);

                                if (events.size() >= 2) {
                                    Collections.sort(events, new Comparator<Event>() {
                                        @Override
                                        public int compare(Event e1, Event e2) {
                                            return e1.getTime().compareTo(e2.getTime()); // Ascending
                                        }
                                    });
                                }

                                mainThreadHandler.post(() -> {
                                    eventDataList = events;
                                    eventArrayAdapter.clear();
                                    for (Event event : events) {
                                        eventArrayAdapter.addEvent(event.getEventID(), event.getEventName(), event.getLocation(), event.getLocationName(), event.getTime(), event.getOrganizer(), event.getOrganizerID(), event.getQrCode(), event.getAttendeeLimit(), event.getAttendees(), event.getPoster());
                                    }
                                    eventArrayAdapter.notifyDataSetChanged();
                                });
                            });
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch events", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapter(ArrayList<Event> events) {
        // Run on UI thread because notifyDataSetChanged() needs to update the UI
        new Handler(Looper.getMainLooper()).post(() -> {
            if (events.size() >= 2) {
                Collections.sort(events, new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        return e1.getTime().compareTo(e2.getTime()); // Ascending
                    }
                });
            }
            eventDataList = events;
            eventArrayAdapter.clear();
            for (Event event : events) {
                eventArrayAdapter.addEvent(event.getEventID(), event.getEventName(), event.getLocation(), event.getLocationName(), event.getTime(), event.getOrganizer(), event.getOrganizerID(), event.getQrCode(), event.getAttendeeLimit(), event.getAttendees(), event.getPoster());
            }
            eventArrayAdapter.notifyDataSetChanged();
        });
    }

    private void setupRealTimeEventUpdates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        String userid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        eventsRef.whereEqualTo("organizerID", userid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        fetchOrganizerEvents();
                    }
                });
    }

    public void fetchLocal(Context context) {
        // Execute in background
        executorService.execute(() -> {
            ArrayList<Event> tempEventDataList = AppDataHolder.getInstance().getOrganizerEvents(context);

            if (tempEventDataList == null) {
                tempEventDataList = new ArrayList<>();
            }
            else {
                if (tempEventDataList.size() >= 2) {
                    Collections.sort(tempEventDataList, new Comparator<Event>() {
                        @Override
                        public int compare(Event e1, Event e2) {
                            // Assuming getTime() returns a Comparable type
                            return e1.getTime().compareTo(e2.getTime()); // Ascending
                        }
                    });
                }
            }
            ArrayList<Event> finalList = new ArrayList<>(tempEventDataList);

            // Post to main thread to update UI components
            mainThreadHandler.post(() -> {
                eventDataList.clear();
                eventDataList = finalList;

                eventArrayAdapter.clear();
                for (Event event : eventDataList) {
                    eventArrayAdapter.addEvent(event.getEventID(), event.getEventName(), event.getLocation(), event.getLocationName(), event.getTime(), event.getOrganizer(), event.getOrganizerID(), event.getQrCode(), event.getAttendeeLimit(), event.getAttendees(), event.getPoster());
                }
                eventArrayAdapter.notifyDataSetChanged(); // This must be on the main thread
            });
        });
    }
}