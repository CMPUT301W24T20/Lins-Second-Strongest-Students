package com.example.qrcodereader.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.qrcodereader.MainActivity;
import com.example.qrcodereader.MyFirebaseMessagingService;
import com.example.qrcodereader.R;
import com.example.qrcodereader.entity.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
public class LaunchSetUp {
    private Context context;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private DocumentReference docRefUser;
    private Location location;

    private User user;
    private String userId;

    public LaunchSetUp(Context context, Location location) {
        this.context = context;
        this.location = location;
    }

    public void setup() {
        initializeFirestore();
        AppDataHolder.getInstance().loadData(context);
        if (!areNotificationsEnabled()) {
            showEnableNotificationsDialog();
        }
        setupNotificationChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setupBroadcastReceiver();
        }
    }

    private void initializeFirestore() {
        String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");
        docRefUser = db.collection("users").document(deviceID);


        /*
            OpenAI, ChatGpt, 06/03/24
            "I need a way to check if the user is in the firebase with ID deviceID and retrieve it,
             or add a new document with ID as deviceID if it is not present"
        */
        docRefUser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Document exists, user is in the collection
                    Log.d("Firestore", "User exists in the collection.");
                    showToast("Welcome Back");
                } else {
                    // Document does not exist, user is not in the collection
                    Log.d("Firestore", "User does not exist in the collection.");
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("name", "");
                    newUser.put("email", "");
                    newUser.put("phone", "");
                    newUser.put("phoneRegion", "");
                    newUser.put("eventsAttended", new HashMap<>());
                    if (location != null) {
                        newUser.put("location", new GeoPoint(location.getLatitude(), location.getLongitude()));
                    } else {
                        newUser.put("location", new GeoPoint(0, 0));
                    }
                    FirebaseMessaging.getInstance().getToken() //Microsoft Copilot 2024, "get FCM token android"
                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String token) {
                                    newUser.put("token", token);
                                }
                            });

                    SetDefaultProfile.generateNoName(new SetDefaultProfile.ProfilePicCallback() {
                        @Override
                        public void onImageURLReceived(String imageURL) {
                            // created default profile picture, thus can now set
                            newUser.put("ProfilePic", imageURL);
                            docRefUser.set(newUser);
                        }
                    });
                    showToast("Made new account");
                }
            } else {
                Log.d("Firestore", "Failed to fetch document: ", task.getException());
                showToast("Failed to fetch account");
            }
        });
        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("name");
                Map<String, Long> eventsAttended = (Map<String, Long>) documentSnapshot.get("attendees");
                GeoPoint location = documentSnapshot.getGeoPoint("location");
                String image = documentSnapshot.getString("ProfilePic");
                user = new User(deviceID, userName, location, eventsAttended, image);
                userId = user.getUserID();
                showToast("Successfully fetch User");
                Log.d("Firestore", "Successfully fetch document: ");
            }
        }).addOnFailureListener(e -> {
            showToast("Failed to fetch User");
        });
    }

    private void showToast(String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }

    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    private void showEnableNotificationsDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Enable Notifications")
                .setMessage("Please enable notifications to receive updates from organizers.")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            openAppSettings();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    /**
     * Open settings to enable notifications
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        context.startActivity(intent);
    }

    private void setupNotificationChannel() {
         /*
        Create notification channel to allow for push notifications
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = context.getString(R.string.default_notification_channel_id);
            String description = context.getString(R.string.title_notifications);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(name, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void setupBroadcastReceiver() {
        Log.d("BroadcastChannel", "Setting up...");
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // See MyFirebaseMessagingService for broadcast
                Log.d("Attempting to recieve...", "onReceive");
                if (MyFirebaseMessagingService.ACTION_BROADCAST.equals(intent.getAction())) {
                    String notificationData = intent.getStringExtra("body"); //key of intent
                    Log.d("Received", notificationData);
                    MainActivity.notificationList.add(notificationData);
                }
            }
        };

        // Register the receiver
        IntentFilter filter = new IntentFilter(MyFirebaseMessagingService.ACTION_BROADCAST);
        context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
    }
}
