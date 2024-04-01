package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.HashMap;
import java.util.Map;

/**
 *  Activity for users to view all events they have created.
 *  <p>
 *      User can press on an event to bring them to EventDetailsOrganizerActivity
 *  </p>
 *  @author Son and Duy and Khushdeep
 */
public class OrganizerEventActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference pastEventsRef;
    private String userid;
    private String username;
    private ListView eventList;
    private EventArrayAdapter eventArrayAdapter;
    private ArrayList<Event> eventDataList;
    private EventFetcher eventFetcher;

    /**
     * This method is called when the activity is starting.
     * It initializes the activity and sets up the Firestore references and ListView.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.organizer_activity_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        pastEventsRef = db.collection("pastEvents");

        eventList = findViewById(R.id.event_list_organizer);
        eventDataList = new ArrayList<>();

        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        eventFetcher = new EventFetcher(eventArrayAdapter, this);


        //String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //fetchEvents(this);

//        eventsRef.whereEqualTo("organizerID", deviceID)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot querySnapshots,
//                                        @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            Log.e("Firestore", error.toString());
//                            return;
//                        }
//                        if (querySnapshots != null) {
//                            eventDataList.clear();
//                            for (QueryDocumentSnapshot doc : querySnapshots) {
//                                String eventID = doc.getId();
//                                String name = doc.getString("name");
//                                GeoPoint location = doc.getGeoPoint("location");
//
//                                String locationName;
//                                if (doc.getString("locationName") != null) {
//                                    locationName = doc.getString("locationName");
//                                } else {
//                                    locationName = "No location";
//                                }
//
//                                Timestamp time = doc.getTimestamp("time");
//                                String organizer = doc.getString("organizer");
//                                String organizerID = doc.getString("organizerID");
//                                String EventPoster = doc.getString("poster");
//
//                                String qrCodeString = doc.getString("qrCode");
//                                QRCode qrCode = new QRCode(qrCodeString);
//                                int attendeeLimit = doc.contains("attendeeLimit") ? (int) (long) doc.getLong("attendeeLimit") : -1;
//                                Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");
//
//                                Log.d("Firestore", "Event fetched");
//                                //Toast.makeText(OrganizerEventActivity.this, "Event fetched", Toast.LENGTH_SHORT).show();
//                                eventArrayAdapter.addEvent(eventID, name, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit,attendees, EventPoster);
//                            }
//                        }
//                    }
//                });

        Button createEventButton = findViewById(R.id.create_event_button);
        createEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerEventActivity.this, CreateEventActivity.class);
            intent.putExtra("userid", userid);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        Button returnButton = findViewById(R.id.return_button_organizer);
        returnButton.setOnClickListener(v -> finish());
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

    protected void onResume() {
        super.onResume();
        fetchEvents(this);
        eventFetcher.fetchOrganizerEvents();
    }

    public void fetchEvents(Context context) {
        eventDataList.clear();
        eventArrayAdapter.clear();
        eventDataList = AppDataHolder.getInstance().getOrganizerEvents(context);

        for (Event event : eventDataList) {
            eventArrayAdapter.addEvent(event.getEventID(), event.getEventName(), event.getLocation(), event.getLocationName(), event.getTime(), event.getOrganizer(), event.getOrganizerID(), event.getQrCode(), event.getAttendeeLimit(), event.getAttendees(), event.getPoster());
        }

        eventArrayAdapter.notifyDataSetChanged();
    }
}