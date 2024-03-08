package com.example.qrcodereader.ui.admin;

import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.R;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;
import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.ui.eventPage.CreateEventActivity;
import com.example.qrcodereader.ui.eventPage.EventDetailsAttendeeActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
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
 *  Activity for admin to browse event, only admins have access. Allow admin to remove events
 * @author Son
 */
public class AdminEventActivity extends AppCompatActivity {


    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private Event selectedEvent = null;
    private boolean isFetching = false;
    private static final int PAGE_SIZE = 10;
    private DocumentSnapshot lastVisible;
    private ArrayList<Event> eventDataList;
    private EventArrayAdapter eventArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        ListView eventList = findViewById(R.id.event_list);
        eventDataList = new ArrayList<>();

        eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);

        fetchEvents();

        eventList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Load more items if we've reached the bottom
                if (!isFetching && (firstVisibleItem + visibleItemCount >= totalItemCount)) {
                    fetchEvents();
                }
            }
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                // Get the item that was clicked
                selectedEvent = eventDataList.get(position);

                // Display a toast with the selected item
                Intent detailIntent = new Intent(AdminEventActivity.this, EventDetailsAttendeeActivity.class);
                detailIntent.putExtra("eventID", selectedEvent.getEventID());
                startActivity(detailIntent);
            }
        });

        Button returnButton = findViewById(R.id.return_button);
        returnButton.setOnClickListener(v -> finish());
    }

    /**
     * Fetches events from Firestore and adds them to the eventDataList
     */
    private void fetchEvents() {
        // Prevents fetching new data if previous request is still in progress
        if (isFetching) {
            return;
        }

        isFetching = true;

        Query query = eventsRef.orderBy("time", Query.Direction.DESCENDING).limit(PAGE_SIZE);
        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Assuming Event class has a constructor matching this data
                        Event event = new Event(doc.getId(), doc.getString("name"),
                                doc.getString("organizer"), doc.getGeoPoint("location"),
                                doc.getTimestamp("time"));
                        eventDataList.add(event);
                    }
                    eventArrayAdapter.notifyDataSetChanged();
                    int lastIndexOfQuery = queryDocumentSnapshots.size() - 1;
                    lastVisible = queryDocumentSnapshots.getDocuments().get(lastIndexOfQuery);
                }
                isFetching = false;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                isFetching = false;
                Log.e("Firestore", "Error fetching events", e);
            }
        });
    }
}
