package com.example.qrcodereader.entity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


/*
            OpenAI, ChatGpt, 05/04/24
            "How to use Dependency Injection in Activities"
        */
public class FirestoreManager {
    private static FirestoreManager instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userCollection;
    private String eventCollection;
    private String userDocRef;
    private String eventDocRef;

    private FirestoreManager() {
        this.userCollection = "users";
        this.eventCollection = "events";
    }

    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }

    public void setUserCollection(String collection) {
        this.userCollection = collection;
    }

    public void setEventCollection(String collection) {
        this.eventCollection = collection;
    }

    public void setUserDocRef(String userDocRef) {
        this.userDocRef = userDocRef;
    }

    public void setEventDocRef(String eventDocRef) {
        this.eventDocRef = eventDocRef;
    }

    public CollectionReference getUserCollection() {
        return db.collection(userCollection);
    }

    public CollectionReference getEventCollection() {
        return db.collection(eventCollection);
    }

    public DocumentReference getUserDocRef() {
        return db.collection(userCollection).document(userDocRef);
    }

    public DocumentReference getEventDocRef() {
        return db.collection(eventCollection).document(eventDocRef);
    }

    public String getUserID() {
        return userDocRef;
    }

    public String getEventID() {
        return eventDocRef;
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}
