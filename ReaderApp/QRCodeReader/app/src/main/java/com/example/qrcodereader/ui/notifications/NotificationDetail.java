package com.example.qrcodereader.ui.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationDetail {
    private String event;
    private String title;
    private String body;

    public NotificationDetail() {}

    public NotificationDetail(String event, String title, String body) {
        this.event = event;
        this.title = title;
        this.body = body;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPoster() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("events").document(this.getEvent());

        //Default poster
        final String[] poster = {"https://firebasestorage.googleapis.com/v0/b/linssecondstrongeststudents.appspot.com/o/EventPoster%2FmeowPICTURE.png?alt=media&token=90ad6f2c-b906-4da8-94db-7389c9451a1c"};
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the Poster field
                        poster[0] = document.getString("Poster");
                        Log.d("PosterRetrieved", "Poster: " + poster[0]);
                    } else {
                        Log.d("PosterNotRetrieved", "No such document");
                    }
                } else {
                    Log.d("PosterFailure", "get failed with ", task.getException());
                }
            }
        });
        return poster[0];
    }
}
