package com.example.qrcodereader;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.provider.Settings;

import java.io.ByteArrayOutputStream;

import android.Manifest;

import com.example.qrcodereader.entity.User;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.BrowseEventActivity;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;
import com.example.qrcodereader.ui.profile.ProfileFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;


import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.qrcodereader.databinding.ActivityMainBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
    private DocumentReference docRefUser;
    private User user;
    private FusedLocationProviderClient fusedLocationClient;

    private StorageReference storageReference;

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

        Bundle bundle = new Bundle();

        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events1");

        docRefUser = db.collection("users1").document(deviceID);
        docRefUser.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot UserField = task.getResult();
                if (task.getResult().exists()) {
                    // Document exists, user is in the collection
                    Log.d("Firestore", "User exists in the collection.");
//                    Toast.makeText(this, "Signed in", Toast.LENGTH_LONG).show();
                } else {
                    // Document does not exist, user is not in the collection
                    Log.d("Firestore", "User does not exist in the collection.");
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("name", "John Doe");
                    newUser.put("eventsAttended", new HashMap<>());
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
//                GeoPoint location = documentSnapshot.getGeoPoint("location");
                user = new User(deviceID, userName, null);
                Toast.makeText(this, "Successfully fetch account", Toast.LENGTH_LONG).show();
                Log.d("Firestore", "Successfully fetch document: ");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to fetch user", Toast.LENGTH_LONG).show();
        });


        CollectionReference ColRefPic = db.collection("DefaultProfilePics");
        int index = (user.getName().length() % 4)+1;
        String P = "P"+index;

        ColRefPic.document("P4").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Get the value of the string field
                        String imageURL = document.getString("URL");
                        Map<String, Object> DefaultProfile = new HashMap<>();
                        DefaultProfile.put("URL", imageURL);
                        docRefUser.set(DefaultProfile);

                        user.setProfilePicture(imageURL);
                        Toast.makeText(MainActivity.this, "Image URL: " + imageURL, Toast.LENGTH_SHORT).show();
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


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // retrieve user's name from DB
        // String userName = task.getResult().getString("name");

        Button profile_button = findViewById(R.id.profile_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("UserName", user.getName());
                bundle.putString("profile_picture", user.getProfilePicture());
                ProfileFragment listfrag = new ProfileFragment();
                listfrag.setArguments(bundle);
                listfrag.show(getSupportFragmentManager(), "Profile Page");
            }
        });

        Button MyEventButton = findViewById(R.id.my_event_button);
        MyEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Choose an action");

                // Button to go to AttendeeEventActivity
                builder.setPositiveButton("Go to Your Event Page (Attendee)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, AttendeeEventActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                });

                // Button to go to OrganizerEventActivity
                builder.setNegativeButton("Go to Your Event Page (Organizer)", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, OrganizerEventActivity.class);
                        intent.putExtra("userID", user.getUserID());
                        intent.putExtra("userName", user.getName());
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



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    user.setLocation(location);
                }
            }
        });

    }
}