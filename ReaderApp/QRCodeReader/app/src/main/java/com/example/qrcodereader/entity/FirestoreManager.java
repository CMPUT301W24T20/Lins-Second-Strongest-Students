package com.example.qrcodereader.entity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


/*
            OpenAI, ChatGpt, 05/04/24
            "How to use Dependency Injection in Activities"
            Microsoft Copilot 4/8/2024
            "Generate java docs for the following class"
*/
/**
 * The FirestoreManager class provides a singleton for Firestore database.
 */
public class FirestoreManager {
    private static FirestoreManager instance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userCollection;
    private String eventCollection;
    private String qrCodeCollection;
    private String userDocRef;
    private String eventDocRef;

    private FirestoreManager() {
        this.userCollection = "users";
        this.eventCollection = "events";
        this.qrCodeCollection = "QRCodes";
    }
    /**
     * Returns the singleton instance of FirestoreManager.
     *
     * @return The singleton instance of FirestoreManager.
     */
    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }
    /**
     * Sets the collection of user documents.
     *
     * @param collection The name of the user collection.
     */
    public void setUserCollection(String collection) {
        this.userCollection = collection;
    }
    /**
     * Sets the collection name for event documents.
     *
     * @param collection The name of the event collection.
     */

    public void setEventCollection(String collection) {
        this.eventCollection = collection;
    }
    /**
     * Sets the collection name for QR code documents.
     *
     * @param collection The name of the QR code collection.
     */
    public void setQrCodeCollection(String collection) {
        this.qrCodeCollection = collection;
    }
    /**
     * Sets the document reference for a user.
     *
     * @param userDocRef The document reference for the user.
     */
    public void setUserDocRef(String userDocRef) {
        this.userDocRef = userDocRef;
    }
    /**
     * Sets the document reference for an event.
     *
     * @param eventDocRef The document reference for the event.
     */

    public void setEventDocRef(String eventDocRef) {
        this.eventDocRef = eventDocRef;
    }
    /**
     * Returns the collection reference for users.
     *
     * @return The collection reference for users.
     */

    public CollectionReference getUserCollection() {
        return db.collection(userCollection);
    }
    /**
     * Returns the collection reference for events.
     *
     * @return The collection reference for events.
     */

    public CollectionReference getEventCollection() {
        return db.collection(eventCollection);
    }
    /**
     * Returns the collection reference for QR codes.
     *
     * @return The collection reference for QR codes.
     */
    public CollectionReference getQrCodeCollection() {
        return db.collection(qrCodeCollection);
    }
    /**
     * Returns the document reference for the specified user.
     *
     * @return The document reference for the specified user.
     */

    public DocumentReference getUserDocRef() {
        return db.collection(userCollection).document(userDocRef);
    }
    /**
     * Returns the document reference for the specified event.
     *
     * @return The document reference for the specified event.
     */

    public DocumentReference getEventDocRef() {
        return db.collection(eventCollection).document(eventDocRef);
    }
    /**
     * Returns the ID of the user document.
     *
     * @return The ID of the user document.
     */
    public String getUserID() {
        return userDocRef;
    }

    /**
     * Returns the ID of the event document.
     *
     * @return The ID of the event document.
     */
    public String getEventID() {
        return eventDocRef;
    }
    /**
     * Returns the Firestore instance.
     *
     * @return The Firestore instance.
     */
    public FirebaseFirestore getDb() {
        return db;
    }
}
