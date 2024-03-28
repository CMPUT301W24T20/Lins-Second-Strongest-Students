package com.example.qrcodereader;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qrcodereader.entity.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
// Microsoft Bing, 2024, COPILOT, Prompted to edit my MapView class to work with accordance to google maps given error descriptions
/**
 * Map Activity
 * @author Khushdeep
 * Represents a MapView for organizing and displaying a Google Map.
 * Implements the OnMapReadyCallback interface.
 */
public class MapViewOrganizer extends AppCompatActivity implements OnMapReadyCallback{
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap map;
    //private User user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.map_view);
        //user = (User) getIntent().getSerializableExtra("user");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmaps);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button buttonBackToMain = findViewById(R.id.buttonBackToMain);
        buttonBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapViewOrganizer.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * Called when the map is ready to be used.
     *
     * @param googleMap The GoogleMap instance.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return;
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng position;
                if (location == null) {
                    position = new LatLng(53.5461, 13.4937);
                } else {
                    position = new LatLng(location.getLatitude(), location.getLongitude());
                }
                //user.setLocation(location);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
            }
        });
        placePins(map);
    }
    /**
     * Called when the user responds to a permission request.
     *
     * @param requestCode The request code passed when requesting permissions.
     * @param permissions The requested permissions.
     * @param grantResults The results of the permission request (either PERMISSION_GRANTED or PERMISSION_DENIED).
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(map);
            } else {

                LatLng defaultLocation = new LatLng(53.5461, 13.4937);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
            }
        }
    }
    // Currently places pins on user location and not checkinLocation ideally make in the scanner a call to grab location and upload to event DB
    /**
     * Retrieves event locations from Firestore and places pins (markers) on the map.
     *
     * @param map The GoogleMap instance where markers will be added.
     */
    private void placePins(GoogleMap map){
        String organizerName = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("events")
                .whereEqualTo("organizer", organizerName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        map.clear(); // clear old markers

                        for (QueryDocumentSnapshot document : snapshots) {
                            Map<String, Long> attendeesMap = (Map<String, Long>) document.get("attendees");
                            if (attendeesMap != null) {
                                for (Map.Entry<String, Long> entry: attendeesMap.entrySet()) {
                                    String userId = entry.getKey();
                                    Long checkInCount = entry.getValue();
                                    if(checkInCount > 0) {
                                        db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");
                                                if (geoPoint != null) {
                                                    LatLng checkInLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                                    map.addMarker(new MarkerOptions().position(checkInLocation).title(document.getString("name")));
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
    }
}
