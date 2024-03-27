package com.example.qrcodereader;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

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
public class Notifier {

    public void notifyUsers(ArrayList<Map.Entry<String, Long>> attendees, Context context) {

        String[] text = prompt(context);
        String title = "Message From an Organizer: " + text[0];
        String body = text[1];

        for (Map.Entry<String, Long> entry : attendees) {
            notify(entry.getKey(), title, body);
        }

    }

    private String[] prompt(Context context) {

        String[] input = new String[2];

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Title");
        alert.setMessage("Message");

        final EditText enterTitle = new EditText(context);
        alert.setView(enterTitle);
        final EditText enterBody = new EditText(context);
        alert.setView(enterBody);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                input[0] = String.valueOf(enterTitle.getText());
                input[1] = String.valueOf(enterBody.getText());
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();

        return input;

    }

    private void notify(String token, String titleText, String bodyText) {
        //Microsoft Copilot, 2024: Using OkHttp for FCM messaging

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

        try {
            Response response = client.newCall(request).execute();
            response.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
