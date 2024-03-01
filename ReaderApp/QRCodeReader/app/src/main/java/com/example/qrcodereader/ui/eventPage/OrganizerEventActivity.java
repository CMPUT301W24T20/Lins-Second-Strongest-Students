package com.example.qrcodereader.ui.eventPage;
import com.example.qrcodereader.R;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.EventArrayAdapter;

import java.util.ArrayList;

public class OrganizerEventActivity extends AppCompatActivity {


//    private FirebaseFirestore db;
//    private CollectionReference eventsRef;

//    private void addNewEvent(Event event) {
//        HashMap<String, String> data = new HashMap<>();
//        data.put("Name", event.getEventName());
//        eventsRef.document(event.getEventName()).set(data);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_activity_event);

//        db = FirebaseFirestore.getInstance();
//        eventsRef = db.collection("events");

        ListView eventList = findViewById(R.id.event_list_attendee);
        ArrayList<Event> eventDataList = new ArrayList<>();




        EventArrayAdapter eventArrayAdapter = new EventArrayAdapter(this, eventDataList);
        eventList.setAdapter(eventArrayAdapter);


//        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot querySnapshots,
//                                @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    Log.e("Firestore", error.toString());
//                    return;
//                }
//                if (querySnapshots != null) {
//                    eventDataList.clear();
//                    for (QueryDocumentSnapshot doc: querySnapshots) {
//                        String event = doc.getId();
//                        String province = doc.getString("Province");
//                        Log.d("Firestore", String.format("Event(%s, %s) fetched", event,
//                                province));
//                        eventDataList.add(new Event(event, province));
//                        eventArrayAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        });
//    }


    }
}

