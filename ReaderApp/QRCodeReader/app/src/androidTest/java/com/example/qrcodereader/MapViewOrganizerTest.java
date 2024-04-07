package com.example.qrcodereader;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.FirestoreManager;

import com.example.qrcodereader.ui.eventPage.EventDetailsAttendeeActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.FirebaseFirestore;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

//@RunWith(AndroidJUnit4.class)
//public class MapViewOrganizerTest {
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    @Rule
//    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
//
//
//
//
//    @Before
//    public void setUp(){
//        // Set FirestoreManager collections and document references
//        FirestoreManager.getInstance().setEventCollection("eventsTest");
//        FirestoreManager.getInstance().setUserCollection("usersTest");
//        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
//        FirestoreManager.getInstance().setEventDocRef("vtLdBOt2ujnXybkviXg9");
//    }
//    @Test
//    public void testSignUpAndMarkerPlacement() {
//        // Generate random latitude and longitude
//        double randomLatitude = -90 + (Math.random() * (90 - (-90)));
//        double randomLongitude = -180 + (Math.random() * (180 - (-180)));
//
//        try (ActivityScenario<MapViewOrganizer> scenario = ActivityScenario.launch(MapViewOrganizer.class)) {
//            scenario.onActivity(activity -> {
//
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//                // Simulate user signing up for the event from the random location
//                simulateUserSignUpFromLocation(randomLatitude, randomLongitude);
//
//                // Verify that a marker is placed at the random location on the map
//                List<Marker> markers = activity.getMarkers();
//                Log.d("MapViewOrganizerTest", "Number of markers: " + markers.size());
//
//                for (Marker marker : markers) {
//                    LatLng position = marker.getPosition();
//                    Log.d("MapViewOrganizerTest", "Marker at: " + position.latitude + ", " + position.longitude);
//                }
//
//                // Check if a marker is placed at the random location
//                assertTrue(checkMarkerPlacedAtLocation(markers, randomLatitude, randomLongitude));
//            });
//        }
//    }
//
//
//    private void simulateUserSignUpFromLocation(double latitude, double longitude) {
//        // Simulate user signing up for the event from the specified location
//        Map<String, Long> attendeesMap = new HashMap<>();
//        attendeesMap.put("1d141a0fd4e29d60", 1L); // Set the check-in count to 1 for the user
//
//        // Add the attendee to the event's attendees map
//        db.collection("eventsTest").document("vtLdBOt2ujnXybkviXg9").update("attendees", attendeesMap);
//
//        // Update the user's location to the specified latitude and longitude
//        Map<String, Object> userLocation = new HashMap<>();
//        userLocation.put("latitude", latitude);
//        userLocation.put("longitude", longitude);
//
//        db.collection("usersTest").document("1d141a0fd4e29d60").update("location", userLocation);
//    }
//
//    private boolean checkMarkerPlacedAtLocation(List<Marker> markers, double latitude, double longitude) {
//        final double EPSILON = 1e-6; // Epsilon for double comparison
//        for (Marker marker : markers) {
//            LatLng position = marker.getPosition();
//            if (Math.abs(position.latitude - latitude) < EPSILON && Math.abs(position.longitude - longitude) < EPSILON) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//}

