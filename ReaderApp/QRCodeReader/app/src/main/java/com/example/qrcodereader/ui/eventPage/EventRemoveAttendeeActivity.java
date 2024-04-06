package com.example.qrcodereader.ui.eventPage;

import com.example.qrcodereader.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.AttendeeArrayAdapter;
import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.entity.QRCode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 *  Activity for users to view details of event they want to sign up to.
 *  <p>
 *      This is where the sign up operation happen
 *  </p>
 *  @author Son and Duy
 */
public class EventRemoveAttendeeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private DocumentReference docRefUser;
    private DocumentReference docRefEvent;
    private Event selectedEvent;
    private final String TAG = "EventRemoveAttendeeActivity";
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
        setContentView(R.layout.activity_event_details_attendee);

        TextView eventNameTextView = findViewById(R.id.event_name);
        TextView eventOrganizerTextView = findViewById(R.id.organizer);
        TextView eventLocationTextView = findViewById(R.id.location);
        TextView eventTimeTextView = findViewById(R.id.time);
        //ListView attendeesListView = findViewById(R.id.event_attendees);
        TextView removeButton = findViewById(R.id.sign_up_button);
        removeButton.setText("Remove");


        docRefEvent = FirestoreManager.getInstance().getEventDocRef();
        docRefUser = FirestoreManager.getInstance().getUserDocRef();
        String eventID = FirestoreManager.getInstance().getEventID();
        String userid = FirestoreManager.getInstance().getUserID();

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
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        removeButton.setOnClickListener(v -> {
            docRefUser.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Map<String, Object> eventsAttended = (Map<String, Object>) documentSnapshot.get("eventsAttended");
                    if (eventsAttended != null && eventsAttended.containsKey(eventID)) {
                        // Prepare the delete operation for the specific eventID
                        docRefUser.update("eventsAttended." + eventID, FieldValue.delete())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event ID deleted from user's eventsAttended."))
                                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event ID from user's eventsAttended", e));
                    }
                }
            }).addOnFailureListener(e -> {
                // Handle the error
                Log.d(TAG, "Can't delete");
                throw new RuntimeException("Can't delete");
            });
            docRefEvent.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Map<String, Object> attendees = (Map<String, Object>) documentSnapshot.get("attendees");
                    if (attendees != null && attendees.containsKey(eventID)) {
                        // Prepare the delete operation for the specific eventID
                        docRefEvent.update("attendees." + eventID, FieldValue.delete())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "User ID deleted from event's attendees."))
                                .addOnFailureListener(e -> Log.w(TAG, "Error deleting User ID from event's attendees.", e));
                    }
                }
            }).addOnFailureListener(e -> {
                // Handle the error
                Log.d(TAG, "Can't delete");
                throw new RuntimeException("Can't delete");
            });
            finish();
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

    public DocumentReference getDocRefUser() {
        return docRefEvent;
    }
}
