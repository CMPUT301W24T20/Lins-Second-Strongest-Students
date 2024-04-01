package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.NavBar;
import com.example.qrcodereader.R;

import android.content.Context;
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


import com.example.qrcodereader.entity.QRCode;
import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.util.AppDataHolder;
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
    /**
     * This method is called when the activity is starting.
     * It initializes the activity, sets up the Firestore references, and populates the ListView with the events attended by the user.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = eventDataList.get(position);
                // Show event details in a dialog
                showEventDetailsDialog(selectedEvent);
            }
        });

//        Button returnButton = findViewById(R.id.return_button_attendee);
//        returnButton.setOnClickListener(v -> finish());

        // Go to BrowseEventActivity
        TextView browseButton = findViewById(R.id.browse_button);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendeeEventActivity.this, BrowseEventActivity.class);
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
        // Fetch events from local storage
        eventDataList.clear();
        eventDataList = AppDataHolder.getInstance().getAttendeeEvents(context);

        for (Event event : eventDataList) {
            eventArrayAdapter.addEvent(event.getEventID(), event.getEventName(), event.getLocation(), event.getLocationName(), event.getTime(), event.getOrganizer(), event.getOrganizerID(), event.getQrCode(), event.getAttendeeLimit(), event.getAttendees(), event.getPoster());
        }
        eventArrayAdapter.notifyDataSetChanged();
    }
}
