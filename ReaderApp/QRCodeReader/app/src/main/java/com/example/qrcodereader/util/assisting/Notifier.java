package com.example.qrcodereader.util.assisting;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.qrcodereader.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.index.qual.LengthOf;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Notifier Class
 * Code for handling sending notifications to users
 */
public final class Notifier {
    private  final Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Notifier notifier;
    private Notifier(Context context){
        this.context = context;
    }

    public static Notifier getInstance(Context context) {
        if (notifier == null) {
            notifier = new Notifier(context);
        }

        return notifier;
    }

    /**
     * notifyUsers
     * Function called to send notification to users
     * @param attendees ArrayList of map entries where the key of each
     *                  entry is the ID of the user to be notified
     * @param text Array of strings for notification, [0] = title
     *             [1] = body
     * @param event ID of event sending notification
     */
    public void notifyUsers(ArrayList<Map.Entry<String, Long>> attendees, String[] text, String event) {

        String title = text[0];
        String body = text[1];

        for (Map.Entry<String, Long> entry : attendees) {
            Log.d("GettingToken", " "+entry);
            getToken(entry.getKey(), title, body, event);
        }

    }

    /**
     * getToken
     * Finds FCM token matching provided user and calls notify on it
     * @param userID ID of user to notify
     * @param title Title of message
     * @param body Body of message
     * @param event Event sending message
     */
    private void getToken(String userID, String title, String body, String event) {
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            MyFirebaseMessagingService.store(userID, title, body, event);
                            String token = documentSnapshot.getString("token");
                            Notifier.this.notify(token, title, body, event);
                            Log.d("Sent To:", "Token=" + token);
                        } else {
                            System.out.println("No such document!");
                            throw new RuntimeException("FCM Token not found");
                        }
                    }
                });

    }

    /**
     * OnInputListener
     * Used to get input from dialog in prompt()
     * synchronously with calling class
     */
    public interface OnInputListener {
        void onInput(String[] input);
    }

    /**
     * Creates and shows a dialog to get notification input
     * @param context Calling context
     * @param listener OnInputListener required to send strings after entry
     */
    public void prompt(Context context, OnInputListener listener) {
        String[] input = new String[2];

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Create Notification: ");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText enterTitle = new EditText(context);
        enterTitle.setHint("Title");
        final EditText enterBody = new EditText(context);
        enterBody.setHint("Body");

        layout.addView(enterTitle);
        layout.addView(enterBody);

        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (enterBody.getText().length() > 0 && enterTitle.getText().length() > 0) {
                    input[0] = String.valueOf(enterTitle.getText());
                    input[1] = String.valueOf(enterBody.getText());
                    listener.onInput(input);  // Pass the input to the listener
                } else {
                    Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void findOrganizer(String eventID, int users) {

        String title = "Event Milestone!";
        String body;
        if (users <= 1) {
            body = "Your first user has signed up!";
        } else {
            body = users + " users have signed up to your event.";
        }

        db.collection("events").document(eventID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String organizerID = documentSnapshot.getString("organizerID");
                            getToken(organizerID, title, body, eventID);
                        } else {
                            System.out.println("No such document!");
                            throw new RuntimeException("FCM Token not found");
                        }
                    }
                });
    }

    /**
     * Notifies an event organizer when their event reaches a number of scans
     * @param eventID ID of event
     * @param milestone Number of users
     */
    public void milestoneNotification(String eventID, int milestone) {
        findOrganizer(eventID, milestone);
    }

    /**
     * Sends a message to provided token with provided details
     * @param token FCM token of receiving device
     * @param titleText Notification title
     * @param bodyText Notification body
     * @param eventID ID of event sending message
     */
    private void notify(String token, String titleText, String bodyText, String eventID) {
        //Microsoft Copilot, 2024: Using OkHttp for FCM messaging
        String FMCAuth = context.getString(R.string.FMCAuth);
        Log.d("Notifying...", "token" + token);

        OkHttpClient client = new OkHttpClient();
        JSONObject data = new JSONObject();
        try {
            data.put("eventID", eventID);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MediaType mediaType = MediaType.parse("application/json");

        String json = String.format(
                "{\"to\": \"%s\", \"notification\": {\"body\": \"%s\", \"title\": \"%s\"}, \"data\": %s}",
                token, bodyText, titleText, data.toString());

        RequestBody body = RequestBody.create(mediaType, json);


        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", FMCAuth)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("Response: ", response.body().string());
                    response.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

}
