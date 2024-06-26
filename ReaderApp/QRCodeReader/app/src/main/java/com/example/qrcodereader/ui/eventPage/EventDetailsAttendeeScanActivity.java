package com.example.qrcodereader.ui.eventPage;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.entity.QRCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


/**
 *  Activity for users to view details of event they want to sign up to.
 *  <p>
 *      This is where the sign up operation happen
 *  </p>
 *  @author Son and Duy
 */
public class EventDetailsAttendeeScanActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private DocumentReference docRefUser;
    private DocumentReference docRefEvent;
    private Event selectedEvent;
    String eventID;
    String userid;
    /**
     * This method is called when the activity is starting.
     * It initializes the activity, sets up the Firestore references, and populates the views with event data.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_event_details_promotional);

        db = FirestoreManager.getInstance().getDb();
        userid = FirestoreManager.getInstance().getUserID();
        eventID = FirestoreManager.getInstance().getEventID();
        docRefEvent = FirestoreManager.getInstance().getEventDocRef();
        docRefUser = FirestoreManager.getInstance().getUserDocRef();


        TextView eventNameTextView = findViewById(R.id.event_name);
        TextView eventOrganizerTextView = findViewById(R.id.organizer);
        TextView eventLocationTextView = findViewById(R.id.location);
        TextView eventTimeTextView = findViewById(R.id.time);
        ImageView eventPoster = findViewById(R.id.event_poster);
        //ListView attendeesListView = findViewById(R.id.event_attendees);


        docRefEvent.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Fetch the event details
                String eventName = documentSnapshot.getString("name");
                GeoPoint location = documentSnapshot.getGeoPoint("location");
                String locationName = documentSnapshot.getString("locationName");
                Timestamp time = documentSnapshot.getTimestamp("time");
                String organizer = documentSnapshot.getString("organizer");
                String organizerID = documentSnapshot.getString("organizerID");
                String qrCodeString = documentSnapshot.getString("qrCode");
                String EventPoster = documentSnapshot.getString("poster");

                QRCode qrCode = new QRCode(qrCodeString);
                int attendeeLimit = documentSnapshot.contains("attendeeLimit") ? (int)(long)documentSnapshot.getLong("attendeeLimit") : -1;
                Map<String, Long> eventsAttended = (Map<String, Long>) documentSnapshot.get("attendees");
                selectedEvent = new Event(eventID, eventName, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit, eventsAttended, EventPoster);

                Toast.makeText(this, "Successfully fetch account", Toast.LENGTH_LONG).show();
                Log.d("Firestore", "Successfully fetch document: ");

                eventNameTextView.setText(eventName);
                String organizerText = organizer;
                eventOrganizerTextView.setText(organizerText);
                eventLocationTextView.setText(locationName);
                String timeText = time.toDate().toString();
                eventTimeTextView.setText(timeText);
                if (EventPoster != null && !EventPoster.isEmpty()) {
                    Picasso.get().load(EventPoster).resize(410, 240).centerInside().into(eventPoster);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        TextView SignUpButton = findViewById(R.id.sign_up_button);
        SignUpButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> newEvent = new HashMap<>();

                // Check if the event is full before allow user to sign up
                if (!selectedEvent.isFull()) {
                    newEvent.put("eventsAttended." + eventID, 0);
                    docRefUser.update(newEvent)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Document updated successfully
                                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Update failed
                                    Log.w("Firestore", "Error updating document", e);
                                }
                            });
                    Map<String, Object> newAttendee = new HashMap<>();
                    newAttendee.put("attendees." + userid, 0);
                    docRefEvent.update(newAttendee)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Document updated successfully
                                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Update failed
                                    Log.w("Firestore", "Error updating document", e);
                                }
                            });
                } else {
                    Toast.makeText(EventDetailsAttendeeScanActivity.this, "Event is full", Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });
        ImageView returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public DocumentReference getDocRefEvent() {
        return docRefEvent;
    }
}
