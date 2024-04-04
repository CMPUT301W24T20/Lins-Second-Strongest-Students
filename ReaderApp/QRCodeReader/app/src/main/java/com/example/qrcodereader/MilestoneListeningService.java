package com.example.qrcodereader;

import static android.app.Service.START_STICKY;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MilestoneListeningService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MilestoneStart", "OnStartCommand");
        listenForMilestones();
        return START_STICKY;
    }

    private void listenForMilestones() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        Notifier notifier = Notifier.getInstance(this);
        Log.d("MilestoneListener", "Listener loading...");

        eventsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String eventId = document.getId();
                        DocumentReference eventRef = eventsRef.document(eventId);

                        // Set up a listener for each event's users subcollection
                        CollectionReference usersRef = eventRef.collection("users");
                        usersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot snapshots,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.d("MilestoneFail", "Snapshot failed");
                                    return;
                                }
                                Log.d("MilestoneListener", "Listener triggered for event: " + eventId);

                                // Get the current number of users
                                int numUsers = snapshots.size();

                                // Assign milestone
                                int milestone = 10;

                                // Check if the number of users has reached the milestone
                                if (numUsers % milestone == 0) {
                                    // Call the milestoneNotify method
                                    notifier.milestoneNotification(eventId, milestone);
                                }
                            }
                        });
                    }
                } else {
                    Log.d("EventMilestoneError", "Error updating/notifying on milestone");
                }
            }
        });
    }

}
