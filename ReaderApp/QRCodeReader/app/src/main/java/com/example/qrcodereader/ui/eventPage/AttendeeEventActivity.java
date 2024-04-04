package com.example.qrcodereader.ui.eventPage;
import static android.content.ContentValues.TAG;

import com.example.qrcodereader.MapView;
import com.example.qrcodereader.NavBar;
import com.example.qrcodereader.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;


import com.example.qrcodereader.entity.QRCode;

import com.example.qrcodereader.util.LaunchSetUp;



import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.util.AppDataHolder;
import com.example.qrcodereader.util.EventFetcher;

import com.example.qrcodereader.util.LocalEventsStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// OpenAI, 2024, ChatGPT, Prompt the error message from logcat and the code snippet that caused the error
/**
 *  Activity for users to browse events they have signed up to.
 *  <p>
 *      Display only events user have signed up in.
 *  </p>
 *  <p>
 *      Can move to BrowseEventActivity with browseButton
 *  </p>
 *  @author Son and Khushdeep and Duy
 */
public class AttendeeEventActivity extends NavBar {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private DocumentReference userDocRef;
    private List<String> attendeeEvents;
    private ArrayList<Event> eventDataList;
    private EventArrayAdapter eventArrayAdapter;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    /**
     * This method is called when the activity is starting.
     * It initializes the activity, sets up the Firestore references, and populates the ListView with the events attended by the user.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LaunchSetUp appSetup = new LaunchSetUp(this);
        appSetup.setup();
        setContentView(R.layout.attendee_events);

        TextView title = findViewById(R.id.upcoming_events);
        title.setText(R.string.AtndTitle);

        setupTextViewButton(R.id.home_button);
        setupTextViewButton(R.id.event_button);
        setupTextViewButton(R.id.scanner_button);
        setupTextViewButton(R.id.notification_button);
        setupTextViewButton(R.id.bottom_profile_icon);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");


        ListView eventList = findViewById(R.id.event_list_attendee);
        eventDataList = new ArrayList<>();
        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        fetchLocal(this);
        setupRealTimeEventUpdates();

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = eventDataList.get(position);
                // Show event details in a dialog
                showEventDetailsDialog(selectedEvent);
            }
        });



        // Go to BrowseEventActivity
        TextView browseButton = findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeEventActivity.this, BrowseEventActivity.class);
                // Use FLAG_ACTIVITY_REORDER_TO_FRONT to bring an existing instance to the front
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        TextView mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeEventActivity.this, MapView.class);
                // Sending the user object to BrowseEventActivity
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.attendee_events;
    }

    /**
     * Show the event details in a dialog
     * @param event The event to show the details of
     */
    private void showEventDetailsDialog(Event event) {
        /*
            OpenAI, ChatGpt, 06/03/24
            "I want to create a dialog box that displays details of an event with customizable design"
        */
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.event_detail_dialog_attendee, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        TextView eventNameTextView = view.findViewById(R.id.event_name);
        String nameText = "Event Name: " + event.getEventName();
        eventNameTextView.setText(nameText);

        TextView eventOrganizerTextView = view.findViewById(R.id.event_organizer);
        String organizerText = "Organizer: " + event.getOrganizer();
        eventOrganizerTextView.setText(organizerText);

        TextView eventLocationTextView = view.findViewById(R.id.event_location);
        String locationText = "Location: " + event.getLocationName();
        eventLocationTextView.setText(locationText);

        TextView eventTimeTextView = view.findViewById(R.id.event_time);
        eventTimeTextView.setText(event.getTime().toDate().toString());

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void fetchLocal(Context context) {
        // Execute in background
        executorService.execute(() -> {
            ArrayList<Event> tempEventDataList = AppDataHolder.getInstance().getAttendeeEvents(context);

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

            // Post to main thread to update UI components
            ArrayList<Event> finalTempEventDataList = tempEventDataList;

            mainThreadHandler.post(() -> {
                eventDataList.clear();
                eventDataList = finalTempEventDataList;

                eventArrayAdapter.clear();
                for (Event event : eventDataList) {
                    eventArrayAdapter.addEvent(event.getEventID(), event.getEventName(), event.getLocation(), event.getLocationName(), event.getTime(), event.getOrganizer(), event.getOrganizerID(), event.getQrCode(), event.getAttendeeLimit(), event.getAttendees(), event.getPoster());
                }
                eventArrayAdapter.notifyDataSetChanged(); // This must be on the main thread
            });
        });
    }

    public void fetchAttendeeEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference eventsRef = db.collection("events");
        CollectionReference usersRef = db.collection("users");

        String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference userDocRef = usersRef.document(deviceID);

        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Map<String, Long> attendeeEvents = (Map<String, Long>) snapshot.get("eventsAttended");
                    if(attendeeEvents != null && !attendeeEvents.isEmpty()) {
                        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                                @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.e("Firestore", error.toString());
                                    return;
                                }
                                if (querySnapshots != null) {
                                    executorService.execute(() -> {
                                        ArrayList<Event> events = new ArrayList<>();

                                        for (QueryDocumentSnapshot doc : querySnapshots) {
                                            String id = doc.getId();

                                            // Check if the user has attended the event
                                            if (attendeeEvents.containsKey(id)) {
                                                String name = doc.getString("name");
                                                GeoPoint location = doc.getGeoPoint("location");
                                                String locationName = doc.getString("locationName");
                                                Timestamp time = doc.getTimestamp("time");
                                                String organizer = doc.getString("organizer");
                                                String organizerID = doc.getString("organizerID");
                                                QRCode qrCode = new QRCode(doc.getString("qrCode"));
                                                int attendeeLimit = doc.getLong("attendeeLimit").intValue();
                                                Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");
                                                String EPoster = doc.getString("EPoster");

                                                Event event = new Event(id, name, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit, attendees, EPoster);

                                                events.add(event);
                                            }
                                        }

                                        LocalEventsStorage.saveEvents(AttendeeEventActivity.this, events, "attendeeEvents.json");
                                        AppDataHolder.getInstance().loadAttendeeEvents(AttendeeEventActivity.this);

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
                            }
                        });
                    } else{
                        Log.d("Firestore", "No events attended by the user");
                    }
                } else {
                    Log.d("Firestore", "Current data: null");
                }
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
        // Assuming you have the user's document ID
        String userId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        // Reference to the user's document
        DocumentReference userDocRef = db.collection("users").document(userId);

        // Listen for real-time updates to the user's document
        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    // Retrieve the updated eventsAttended map
                    Map<String, Long> updatedEventsAttended = (Map<String, Long>) snapshot.get("eventsAttended");
                    if (updatedEventsAttended != null && !updatedEventsAttended.isEmpty()) {
                        // Fetch the details of the attended events based on the updated map
                        fetchAttendeeEvents();
                    }
                } else {
                    Log.d(TAG, "Current user data: null");
                }
            }
        });
    }

}