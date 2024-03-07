package com.example.qrcodereader;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.provider.Settings;

import java.util.concurrent.ExecutionException;
import android.Manifest;

import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.BrowseEventActivity;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.example.qrcodereader.ui.profile.ProfileFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.qrcodereader.databinding.ActivityMainBinding;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static ArrayList<String> notificationList = new ArrayList<>();

    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private DocumentReference docRefUser;
    private User user;
    public static String userId;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        docRefUser = db.collection("users").document(deviceID);
        docRefUser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Document exists, user is in the collection
                    Log.d("Firestore", "User exists in the collection.");
                    Toast.makeText(this, "Signed in", Toast.LENGTH_LONG).show();
                } else {
                    // Document does not exist, user is not in the collection
                    Log.d("Firestore", "User does not exist in the collection.");
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("name", "John Doe");
                    newUser.put("eventsAttended", new HashMap<>());
                    newUser.put("location", new GeoPoint(0,0));
                    docRefUser.set(newUser);
                    Toast.makeText(this, "Made new account", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("Firestore", "Failed to fetch document: ", task.getException());
                Toast.makeText(this, "Failed to fetch account", Toast.LENGTH_LONG).show();
            }
        });
        docRefUser.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("name");
                Map<String, Long> eventsAttended = (Map<String, Long>) documentSnapshot.get("attendees");
                GeoPoint location = documentSnapshot.getGeoPoint("location");
                user = new User(deviceID, userName, location, eventsAttended);
                Toast.makeText(this, "Successfully fetch account", Toast.LENGTH_LONG).show();
                Log.d("Firestore", "Successfully fetch document: ");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });


//        Location Linlocation = new Location("provider");
//        Linlocation.setLatitude(61.0137);
//        Linlocation.setLongitude(99.1967);
//        User user = new User(deviceID, "Guohui Lin", Linlocation);
//        Intent intent = new Intent(this, BrowseEventActivity.class);
//        intent.putExtra("user", user);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //Firebase Cloud Messaging Token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                   @Override
                   public void onComplete(@NonNull Task<String> task) {
                       if (!task.isSuccessful()) {
                           Log.w("FCM_Fail", "Fetching FCM registration token failed", task.getException());
                           return;
                       }

                       // Get new FCM registration token
                       String token = task.getResult();

                       // Log and toast
                       String msg = getString(R.string.msg_token_fmt, token);
                       Log.d("FCM_Success", msg);
                   }
               });

        // I'm working on this part - Duy
        Button profile_button = findViewById(R.id.profile_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("UserName", user.getName());
                ProfileFragment listfrag = new ProfileFragment();
                listfrag.setArguments(bundle);
                listfrag.show(getSupportFragmentManager(), "Profile Page");
            }
        });



        //Notification Channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("default_channel",
                    "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Default Channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Broadcast Receiver to store notifications
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // See MyFirebaseMessagingService for broadcast
                //TO-DO:
                Log.d("Attempting to recieve...", "onReceive");
                if (MyFirebaseMessagingService.ACTION_BROADCAST.equals(intent.getAction())) {
                    String notificationData = intent.getStringExtra("body"); //key of intent
                    Log.d("Received", notificationData);
                    notificationList.add(notificationData);
                }

            }
        };

        Button MyEventButton = findViewById(R.id.my_event_button);
        MyEventButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Choose an action");

            // Button to go to AttendeeEventActivity
            builder.setPositiveButton("Go to Your Event Page (Attendee)", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MainActivity.this, AttendeeEventActivity.class);
                    startActivity(intent);
                }
            });

            // Button to go to OrganizerEventActivity
            builder.setNegativeButton("Go to Your Event Page (Organizer)", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MainActivity.this, OrganizerEventActivity.class);
                    startActivity(intent);
                }
            });

            // Cancel button
            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            }
        });

//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
//                new IntentFilter(MyFirebaseMessagingService.ACTION_BROADCAST));
//
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//            return;
//        }
//        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    user.setLocation(location);
//                }
//            }
//        });


    }
}