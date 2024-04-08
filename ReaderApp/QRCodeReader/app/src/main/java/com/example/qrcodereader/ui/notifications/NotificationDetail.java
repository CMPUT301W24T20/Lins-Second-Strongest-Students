package com.example.qrcodereader.ui.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.qrcodereader.entity.FirestoreManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * NotificationDetail
 * Class containing code representing a notification
 * stored in firebase within a subcollection for a user
 */
public class NotificationDetail {
    private String event;
    private String title;
    private String body;
    private String userID = FirestoreManager.getInstance().getUserID();

    /**
     * DeleteDocumentID()
     * Finds the documentID for a notification and calls RemoveFromFirebase
     */
    private void DeleteDocumentId() {

        final String[] id = new String[1];
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userID);

        userRef.collection("notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("GetID", document.getId() + " => " + document.getData());
                        RemoveFromFirebase(document.getId());
                    }
                } else {
                    Log.w("GetIDFail", "Error getting documents.", task.getException());
                }
            }
        });
    }

    /**
     * RemoveFromFirebase
     * Deletes a notifcation document from firebase
     * @param docID ID of document to delete
     */
    private void RemoveFromFirebase(String docID) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userID);
        DocumentReference notificationRef = userRef.collection("notifications").document(docID);

        notificationRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DeleteNotif", "Notification successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DeleteNotifFail", "Error deleting notification", e);
                    }
                });

    }

    //Default constructor
    public NotificationDetail() {}

    //Loaded constructor
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

    /**
     * Gets the URL for a notification's poster
     * @return URL of poster
     */
    public String getPoster() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("events").document(this.getEvent());

        //Default poster
        final String[] poster = {null};
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

    /**
     * delete()
     * Deletes this notification from firebase
     */
    public void delete() {

        this.DeleteDocumentId();

    }
}
