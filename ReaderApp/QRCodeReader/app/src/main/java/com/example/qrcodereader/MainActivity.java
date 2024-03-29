package com.example.qrcodereader;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.provider.Settings;

import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.ui.admin.AdminEventActivity;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.example.qrcodereader.ui.profile.ProfileFragment;

import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.qrcodereader.databinding.ActivityMainBinding;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MainActivity, the start point of the program
 * <p>
 *     Houses the Profile, Events, Map, Admin access button at the beginning of the app
 * </p>
 * @author all
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static ArrayList<String> notificationList = new ArrayList<>();
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private DocumentReference docRefUser;

    public User user;
    public static String userId;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * This method is called when the activity is starting.
     * It initializes the activity, sets up the Firestore references, and sets up the views for the main activity.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeFirestore();
        setupNavigation();
        setupProfileButton();
        setupNotificationChannel();
        setupBroadcastReceiver();
        setupMyEventButton();
        setupMapButton();
        checkAdminStatus();
    }

    /**
     * Initializes Firestore and sets up the user document reference.
     */
    private void initializeFirestore() {
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
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
                    Toast.makeText(this, "Welcome Back", Toast.LENGTH_LONG).show();
                } else {
                    // Document does not exist, user is not in the collection
                    Log.d("Firestore", "User does not exist in the collection.");
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("name", "");
                    newUser.put("email", "");
                    newUser.put("phone", "");
                    newUser.put("phoneRegion", "");
                    newUser.put("eventsAttended", new HashMap<>());
                    newUser.put("location", new GeoPoint(0,0));

                    // set default profile
                    CollectionReference ColRefPic = db.collection("DefaultProfilePics");
                    ColRefPic.document("P4").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    // Get the value of the string field
                                    String imageURL = document.getString("URL");
                                    newUser.put("ProfilePic", imageURL);
                                    docRefUser.set(newUser);

//                                    Toast.makeText(MainActivity.this, "Image URL: " + imageURL, Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d("Firestore", "No such document");
                                    Toast.makeText(MainActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Firestore", "Error getting document", task.getException());
                                Toast.makeText(MainActivity.this, "Error getting document: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

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
                String image = documentSnapshot.getString("ProfilePic");
                user = new User(deviceID, userName, location, eventsAttended, image);
                Toast.makeText(this, "Successfully fetch account", Toast.LENGTH_LONG).show();
                Log.d("Firestore", "Successfully fetch document: ");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });

        //        int index = (user.getName().length() % 4)+1;
//        String P = "P"+index;
//

    }



    /**
     * Sets up the navigation for the main activity.
     */
    private void setupNavigation() {
        /*
        Configure navigation bar
         */
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**
     * Sets up the profile button for the main activity.
     */
    private void setupProfileButton() {
        Button profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment listfrag = new ProfileFragment();
                listfrag.show(getSupportFragmentManager(), "Profile Page");
            }
        });
    }



    /**
     * setupNotificationChannel
     * Creates notification channel for app
     */
    private void setupNotificationChannel() {
        /*
        Create notification channel to allow for push notifications
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("default_channel",
                    "Default Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Default Channel");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * setupBroadcastReceiver
     * Adds incoming notifications from FirebaseMessagingService
     * to an arraylist
     */
    private void setupBroadcastReceiver() {
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
    }

    /**
     * Sets up the 'My Event' button for the main activity.
     * This button opens a dialog that allows the user to navigate to their event page as an attendee or organizer.
     */
    private void setupMyEventButton() {
        /*
            OpenAI, ChatGpt, 01/03/24
            "I want to create a dialog box with three option, two of the options go to two different Activity,
            and the final option is to cancel"
        */
        Button myEventButton = findViewById(R.id.my_event_button);
        myEventButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Choose Event Page");

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
    }

    /**
     * Sets up the 'Map' button for the main activity.
     * This button opens a dialog that allows the user to navigate to the map as an attendee or organizer.
     */
    private void setupMapButton() {
        Button mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Choose Map You Want to See");

                // Button to go to AttendeeEventActivity
                builder.setPositiveButton("Go to Map (Attendee)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, MapView.class);
                        startActivity(intent);
                    }
                });

                // Button to go to OrganizerEventActivity
                builder.setNegativeButton("Go to Map(Organizer)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, MapViewOrganizer.class);
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
    }

    /**
     * Checks the admin status of the user.
     * If the user is an admin, it sets up the 'Admin' button to open a dialog that allows the user to navigate to different admin pages.
     * If the user is not an admin, it sets up the 'Admin' button to display a toast message saying "Not An Admin. No Access."
     */
    private void checkAdminStatus() {
        /*
            OpenAI, ChatGPT, 07/03/24
            "I want the program to check if the deviceID is in the administrator collection as ID.
            If it is then the button will display the dialog box. Otherwise it will not.
         */
        final boolean[] isAdmin = {false};
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        db.collection("administrator")
                .document(deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        isAdmin[0] = true;
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });

        Button adminButton = findViewById(R.id.admin_button);
        adminButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if (isAdmin[0]) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Choose Action (Click Outside to Cancel)");

                    // Button to go to AttendeeEventActivity
                    builder.setPositiveButton("View Events", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(MainActivity.this, AdminEventActivity.class);
                            startActivity(intent);
                        }
                    });

                    // Button to go to OrganizerEventActivity
                    builder.setNegativeButton("View Profiles", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    // Cancel button
                    builder.setNeutralButton("View Pictures", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(MainActivity.this, "Not An Admin. No Access.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

















