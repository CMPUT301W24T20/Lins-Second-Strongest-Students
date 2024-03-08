package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.R;

import android.content.Intent;
import android.os.Bundle;
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


import com.example.qrcodereader.entity.User;
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
import java.util.List;
import java.util.Map;

public class AttendeeEventActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private DocumentReference userDocRef;
    private List<String> attendeeEvents;
//    private void addNewEvent(Event event) {
//        HashMap<String, String> data = new HashMap<>();
//        data.put("Name", event.getEventName());
//        eventsRef.document(event.getEventName()).set(data);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_activity_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        usersRef = db.collection("users");


        ListView eventList = findViewById(R.id.event_list_attendee);
        ArrayList<Event> eventDataList = new ArrayList<>();
        EventArrayAdapter eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        // Getting user through MainActivity. This is the user who is using the app
        String userid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        userDocRef = usersRef.document(userid);
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

                    eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshots,
                                            @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.e("Firestore", error.toString());
                                return;
                            }
                            if (querySnapshots != null) {
                                eventDataList.clear();
                                for (QueryDocumentSnapshot doc: querySnapshots) {
                                    String eventID = doc.getId();

                                    // Check if the user has attended the event
                                    if (attendeeEvents.containsKey(eventID)) {
                                        String name = doc.getString("name");
                                        String organizer = doc.getString("organizer");
                                        GeoPoint location = doc.getGeoPoint("location");
                                        Timestamp time = doc.getTimestamp("time");
                                        Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");

                                        Log.d("Firestore", "Event fetched");
                                        eventArrayAdapter.addEvent(eventID, name, organizer, location, time);
                                    }
                                }
                            }
                        }
                    });
                } else {
                    Log.d("Firestore", "Current data: null");
                }
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = eventDataList.get(position);
                // Show event details in a dialog
                showEventDetailsDialog(selectedEvent);
            }
        });

        Button returnButton = findViewById(R.id.return_button_attendee);
        returnButton.setOnClickListener(v -> finish());

        // Go to BrowseEventActivity
        Button browseButton = findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeEventActivity.this, BrowseEventActivity.class);
                // Sending the user object to BrowseEventActivity
                startActivity(intent);
            }
        });
    }

    private void showEventDetailsDialog(Event event) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.event_detail_dialog_attendee, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        // Set the event details to the TextViews
        TextView eventNameTextView = view.findViewById(R.id.event_name);
        String nameText = "Event Name: " + event.getEventName();
        eventNameTextView.setText(nameText);

        TextView eventOrganizerTextView = view.findViewById(R.id.event_organizer);
        String organizerText = "Organizer: " + event.getOrganizer();
        eventOrganizerTextView.setText(organizerText);

        TextView eventLocationTextView = view.findViewById(R.id.event_location);
        String locationText = "Location: " + event.getLocation().getLatitude() + ", " + event.getLocation().getLongitude();
        eventLocationTextView.setText(locationText);

        TextView eventTimeTextView = view.findViewById(R.id.event_time);
        eventTimeTextView.setText(event.getTime().toDate().toString());





        // Create and show the dialog

        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}





