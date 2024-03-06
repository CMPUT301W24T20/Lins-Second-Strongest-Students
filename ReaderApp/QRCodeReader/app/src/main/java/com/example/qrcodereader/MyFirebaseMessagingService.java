package com.example.qrcodereader;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /*
    MyFirebaseMessagingService
    Code written through aid of Microsoft Copilot
    "I need a way to send notifications through firebase"
    March 5th, 2024

    Handles notifications from Firebase
     */

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage.getData().get("message"));
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("NotNullNotif", "Message Body:" + remoteMessage.getNotification().getBody());
        }

    }

    private void sendNotification(String messageBody){

        Context context = MyFirebaseMessagingService.this;

        Intent intent= new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(getString(R.string.firebase_message_title))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        broadcast(messageBody);

    }

    private void broadcast(String messageBody) {
        //TO-DO:
        //Create class to represent notifs
        //Send the class data instead of these placeholders
        Intent intent = new Intent("BROADCAST_NOTIFICATION");
        intent.putExtra("title", "PLACEHOLDERTITLE");
        intent.putExtra("body", messageBody);
        sendBroadcast(intent);
    }

}
