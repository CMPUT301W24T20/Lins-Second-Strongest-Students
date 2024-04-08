package com.example.qrcodereader.util.assisting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.old.MainActivity;
import com.example.qrcodereader.R;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;


/**
 * MyFirebaseMessagingService
 * Allows for reception, display and handling of
 * FCM messages as notifications
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /*
    MyFirebaseMessagingService
    Code written through aid of Microsoft Copilot
    "I need a way to send notifications through firebase"
    March 5th, 2024

    Handles notifications from Firebase
     */

    public static final String ACTION_BROADCAST = MyFirebaseMessagingService.class.getName() + "Broadcast";

    @Override
    public void onNewToken(@NonNull String token) {

        super.onNewToken(token);

    }

    /**
     * onMessageReceived(RemoteMessage)
     * Logs message and calls sendNotification with
     * message contents
     * @param remoteMessage Remote message that has been received.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0 && remoteMessage.getNotification().getBody() != null) {
            Log.d("NotNullNotif", "Message Body:" + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getData().get("eventID"));
        }

    }

    /**
     * sendNotification(String)
     * Sends a push notification upon receiving an FCM message
     * @param title Title of notification
     * @param messageBody Contents of the FCM message
     * @param eventID ID of event that sends message
     */
    private void sendNotification(String title, String messageBody, String eventID){
        Context context = MyFirebaseMessagingService.this;

        Intent intent= new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
        Log.d("Notify", messageBody);

        broadcast(messageBody);
    }

    /**
     * Store
     * Stores notification on firebase under document for user
     * @param title title of message
     * @param messageBody body of message
     * @param eventID ID of event sending message
     */
    public static void store(String userID, String title, String messageBody, String eventID) {
        //Microsoft Copilot, 2024, add map to firestore doc
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("event", eventID);
        data.put("title", title);
        data.put("body", messageBody);

        // Add a new document to the 'notifications' collection of the user document
        db.collection("users").document(userID).collection("notifications").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Notification Stored", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Notification Not Stored", "Error adding document", e);
                    }
                });
    }

    private void broadcast(String messageBody) {
        /*
        Send broadcast containing message details
         */
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra("body", messageBody);
        Log.d("Sending...", messageBody);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

}