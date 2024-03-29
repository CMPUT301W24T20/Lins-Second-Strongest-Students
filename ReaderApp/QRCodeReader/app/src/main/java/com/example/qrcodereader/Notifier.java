package com.example.qrcodereader;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Notifier notifier;
    private Notifier(){}

    public static Notifier getInstance() {
        if (notifier == null) {
            notifier = new Notifier();
        }

        return notifier;
    }

    public void notifyUsers(ArrayList<Map.Entry<String, Long>> attendees, String[] text) {

        String title = "Message From an Organizer: " + text[0];
        String body = text[1];

        for (Map.Entry<String, Long> entry : attendees) {
            getToken(entry.getKey(), title, body);
        }

    }

    private void getToken(String userID, String title, String body) {
        db.collection("users").document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String token = documentSnapshot.getString("token");
                            Notifier.this.notify(token, title, body);
                        } else {
                            System.out.println("No such document!");
                            throw new RuntimeException("FCM Token not found");
                        }
                    }
                });

    }

    public interface OnInputListener {
        void onInput(String[] input);
    }

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
                input[0] = String.valueOf(enterTitle.getText());
                input[1] = String.valueOf(enterBody.getText());
                listener.onInput(input);  // Pass the input to the listener
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }


    private void notify(String token, String titleText, String bodyText) {
        //Microsoft Copilot, 2024: Using OkHttp for FCM messaging
        Log.d("Notifying...", "token" + token);

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        String json = String.format(
                "{\n    \"to\" : \"%s\",\n    \"notification\" : {\n        \"body\" : \"%s\",\n        \"title\": \"%s\"\n    }\n}",
                token, bodyText, titleText
        );

        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "key=AAAAaYKnoe0:APA91bFKZRTGUhZq9coHi_6e3O3aGLzP-WVrTGt2nozwceiIkFvMO-Jy1-fA6UkG4oLHaoDYmjSto9QOeVVeOwYMYbBIQL8cu99pREFuNQ-Eo7tH4hS1uqlEyMgkKA00bNcMgIDgx319")
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
