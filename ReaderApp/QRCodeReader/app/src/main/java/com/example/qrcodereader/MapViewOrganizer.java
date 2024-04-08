package com.example.qrcodereader;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    GoogleMap map;
    String eventID;
    //private User user;
    private List<Marker> markers = Collections.synchronizedList(new ArrayList<>());

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.map_view);

        // Set the eventID from intent extras
        eventID = getIntent().getStringExtra("eventID");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.gmaps);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button buttonBackToMain = findViewById(R.id.buttonBackToMain);
        buttonBackToMain.setOnClickListener(v -> finish());
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

        double latitude = getIntent().getDoubleExtra("latitude", 53.5461);
        double longitude = getIntent().getDoubleExtra("longitude", 113.4937);
        LatLng eventLocation = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 10));
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
        if (map == null) {
            Log.d("PlacePins", "GoogleMap object is null");
            return;
        }
        Log.d("PlacePins", "Method called");
        db.collection("events").document(eventID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("PlacePins", "Document fetched successfully");
                map.clear(); // clear old markers
                synchronized (markers) {
                    markers.clear(); // Clear old markers list
                }

                Map<String, Long> attendeesMap = (Map<String, Long>) documentSnapshot.get("attendees");
                if (attendeesMap != null) {
                    Log.d("PlacePins", "Attendees map is not null");
                    for (Map.Entry<String, Long> entry: attendeesMap.entrySet()) {
                        String userId = entry.getKey();
                        Long checkInCount = entry.getValue();
                        if(checkInCount > 0) {
                            Log.d("PlacePins", "Check-in count is greater than 0 for user: " + userId);
                            db.collection("users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");
                                    if (geoPoint != null) {
                                        Log.d("PlacePins", "GeoPoint is not null for user: " + userId);
                                        LatLng checkInLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                                        Log.d("PlacePins", "Latitude: " + geoPoint.getLatitude() + ", Longitude: " + geoPoint.getLongitude());
                                        Marker marker = map.addMarker(new MarkerOptions().position(checkInLocation).title(documentSnapshot.getString("name")));
                                        marker.setTag(userId); // Set the tag to the userId
                                        markers.add(marker); // Add the marker to the list
                                        Log.d("PlacePins", "Marker added for user: " + userId);
                                        
                                    } else {
                                        Log.d("PlacePins", "GeoPoint is null for user: " + userId);
                                    }
                                }
                            });
                        } else {
                            Log.d("PlacePins", "Check-in count is 0 or less for user: " + userId);
                        }
                    }
                } else {
                    Log.d("PlacePins", "Attendees map is null");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("PlacePins", "Failed to fetch document", e);
            }
        });
    }

    public List<Marker> getMarkers() {
        synchronized (markers) {
            return new ArrayList<>(markers); // Return a copy of the markers list
        }
    }

    // Method to add a test marker
    public void addTestMarker(double latitude, double longitude) {
        if (map != null) {
            LatLng testLocation = new LatLng(latitude, longitude);
            Marker testMarker = map.addMarker(new MarkerOptions().position(testLocation).title("Test Marker"));
            synchronized (markers) {
                markers.add(testMarker);
            }
        }
    }
}
