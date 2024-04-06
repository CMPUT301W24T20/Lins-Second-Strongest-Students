package com.example.qrcodereader.ui.eventPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;
import com.example.qrcodereader.entity.QRCode;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *  Activity for users to browse events they have created that have passed.
 *  <p>
 *      Main purpose is for user to choose a QR code to reuse
 *  </p>
 *  @author Duy
 */
public class CreateEventActivityBrowsePastEvent extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private String userid;
    private String username;
    private ListView eventList;
    private EventArrayAdapter eventArrayAdapter;
    ArrayList<Event> eventDataList;
    /**
     * This method is called when the activity is starting.
     * It initializes the activity, sets up the Firestore references, and populates the ListView with past events organized by the user.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.organizer_past_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        eventList = findViewById(R.id.event_list);
        eventDataList = new ArrayList<>();

        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        // Get the current time as a Timestamp
        Timestamp currentTime = Timestamp.now();

        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        eventsRef.whereLessThan("time", currentTime)
                .whereNotEqualTo("qrCode", "-1")
                .whereEqualTo("organizerID", deviceID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                String name = doc.getString("name");
                                GeoPoint location = doc.getGeoPoint("location");

                                String locationName;
                                if (doc.getString("locationName") != null) {
                                    locationName = doc.getString("locationName");
                                } else {
                                    locationName  = "No location";
                                }

                                Timestamp time = doc.getTimestamp("time");
                                String organizer = doc.getString("organizer");
                                String organizerID = doc.getString("organizerID");
                                String EventPoster = doc.getString("poster");
                                String qrCodeString = doc.getString("qrCode");
                                QRCode qrCode = new QRCode(qrCodeString);
                                int attendeeLimit = doc.contains("attendeeLimit") ? (int)(long)doc.getLong("attendeeLimit") : -1;
                                Map<String, Long> attendees = (Map<String, Long>) doc.get("attendees");

                                Log.d("Firestore", "Event fetched");
                                Toast.makeText(CreateEventActivityBrowsePastEvent.this, "Event fetched", Toast.LENGTH_SHORT).show();
                                eventArrayAdapter.addEvent(eventID, name, location, locationName, time, organizer, organizerID, qrCode, attendeeLimit, attendees, EventPoster);
                            }
                        }
                    }
                });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = eventDataList.get(position);
                QRCode qrCode = selectedEvent.getQrCode();
                String qrCodeString = qrCode.getString();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedQRCode", qrCodeString);
                resultIntent.putExtra("selectedEventID", selectedEvent.getEventID());

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        TextView back = findViewById(R.id.return_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedEventID", "none");

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}

