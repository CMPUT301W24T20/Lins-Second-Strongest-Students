package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.R;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;


import com.example.qrcodereader.entity.QRCode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
        /*
            OpenAI, ChatGpt, 28/03/2024
            "(Fed previous un-paginated query) adjust it to use pagination"
        */
public class AttendeeEventActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private CollectionReference usersRef;
    private DocumentReference userDocRef;
    private Map<String, Long> attendeeEvents;
    private DocumentSnapshot lastVisible;
    private EventArrayAdapter eventArrayAdapter;
    /**
     * This method is called when the activity is starting.
     * It initializes the activity, sets up the Firestore references, and populates the ListView with the events attended by the user.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
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
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                attendeeEvents = (Map<String, Long>) snapshot.get("eventsAttended");
                if(attendeeEvents != null && !attendeeEvents.isEmpty()) {
                    loadPage();
                } else{
                    Log.d("Firestore", "No events attended by the user");
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
        eventList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // No-op
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount != 0) {
                    // The user has scrolled to the bottom, load the next page
                    loadPage();
                }
            }
        });
    }
    private void loadPage() {
        List<String> eventIDs = new ArrayList<>(attendeeEvents.keySet());

        // Determine the start and end indices for the chunk of event IDs
        int start = lastVisible == null ? 0 : eventIDs.indexOf(lastVisible.getId()) + 1;
        int end = Math.min(start + 10, eventIDs.size());

        // Get the chunk of event IDs
        List<String> chunk = eventIDs.subList(start, end);

        Query eventsQuery = eventsRef.whereIn("eventID", chunk).orderBy("time", Query.Direction.DESCENDING);
        eventsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String eventID = document.getId();
                        String name = document.getString("name");
                        String organizer = document.getString("organizer");
                        GeoPoint location = document.getGeoPoint("location");
                        Timestamp time = document.getTimestamp("time");
                        String locationName = document.getString("locationName");
                        String organizerID = document.getString("organizerID");
                        String qrCodeString = document.getString("qrCode");
                        QRCode qrCode = new QRCode(qrCodeString);
                        int attendeeLimit = document.contains("attendeeLimit") ? (int)(long)document.getLong("attendeeLimit") : -1;
                        Map<String, Long> attendees = (Map<String, Long>) document.get("attendees");

                        Log.d("Firestore", "Event fetched");
                        eventArrayAdapter.addEvent(eventID, name, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit,attendees);

                        lastVisible = document;
                    }

                    if (task.getResult().isEmpty()) {
                        Toast.makeText(AttendeeEventActivity.this, "No more events to load", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Firestore", "Error getting documents: ", task.getException());
                }
            }
        });
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
}





