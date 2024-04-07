package com.example.qrcodereader;

import androidx.test.core.app.ActivityScenario;
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


import static org.junit.Assert.assertTrue;


import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class MapViewOrganizerTest {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CountDownLatch latch = new CountDownLatch(1);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        // Set FirestoreManager collections and document references
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("vtLdBOt2ujnXybkviXg9");
    }

    @Test
    public void testSignUpAndMarkerPlacement() {
        // Fixed test location
        double testLatitude = 37.422;
        double testLongitude = -122.084;

        try (ActivityScenario<MapViewOrganizer> scenario = ActivityScenario.launch(MapViewOrganizer.class)) {
            scenario.onActivity(activity -> {
                // Simulate user signing up for the event from the test location
                simulateUserSignUpFromLocation(testLatitude, testLongitude);

                // Wait for Firestore operations to complete
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Verify that a marker is placed at the test location on the map
                List<Marker> markers = activity.getMarkers();
                assertTrue("Marker should be placed at the test location", checkMarkerPlacedAtLocation(markers, testLatitude, testLongitude));
            });
        }
    }

    private void simulateUserSignUpFromLocation(double latitude, double longitude) {
        Map<String, Long> attendeesMap = new HashMap<>();
        attendeesMap.put("1d141a0fd4e29d60", 2L); // Set the check-in count for the user

        db.collection("eventsTest").document("vtLdBOt2ujnXybkviXg9").update("attendees", attendeesMap)
                .addOnCompleteListener(task -> {
                    Map<String, Object> userLocation = new HashMap<>();
                    userLocation.put("latitude", latitude);
                    userLocation.put("longitude", longitude);

                    db.collection("usersTest").document("1d141a0fd4e29d60").update("location", userLocation)
                            .addOnCompleteListener(task1 -> latch.countDown());
                });
    }

    private boolean checkMarkerPlacedAtLocation(List<Marker> markers, double latitude, double longitude) {
        final double EPSILON = 1e-6;
        for (Marker marker : markers) {
            LatLng position = marker.getPosition();
            if (Math.abs(position.latitude - latitude) < EPSILON && Math.abs(position.longitude - longitude) < EPSILON) {
                return true;
            }
        }
        return false;
    }
}


