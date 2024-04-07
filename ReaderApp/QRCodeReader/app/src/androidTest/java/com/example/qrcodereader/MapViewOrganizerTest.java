package com.example.qrcodereader;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.FirestoreManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.FirebaseFirestore;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertTrue;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class MapViewOrganizerTest {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ActivityScenarioRule<MapViewOrganizer> activityRule = new ActivityScenarioRule<>(MapViewOrganizer.class);

    @Test
    public void testSignUpAndMarkerPlacement() {
        // Set FirestoreManager collections and document references
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("vtLdBOt2ujnXybkviXg9");

        // Define a random location for the user to sign up from
        double randomLatitude = 40.7128; // Random latitude
        double randomLongitude = -74.0060; // Random longitude

        try (ActivityScenario<MapViewOrganizer> scenario = ActivityScenario.launch(MapViewOrganizer.class)) {
            // Wait for the map to be ready
            Thread.sleep(5000);

            // Simulate user signing up for the event from the random location
            simulateUserSignUpFromLocation(randomLatitude, randomLongitude);

            // Verify that a marker is placed at the random location on the map
            scenario.onActivity(activity -> {
                List<Marker> markers = activity.getMarkers();

                // Check if a marker is placed at the random location
                assertTrue(checkMarkerPlacedAtLocation(markers, randomLatitude, randomLongitude));
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void simulateUserSignUpFromLocation(double latitude, double longitude) {
        // Simulate user signing up for the event from the specified location
        Map<String, Long> attendeesMap = new HashMap<>();
        attendeesMap.put("1d141a0fd4e29d60", 1L); // Set the check-in count to 1 for the user

        // Add the attendee to the event's attendees map
        db.collection("events").document("vtLdBOt2ujnXybkviXg9").update("attendees", attendeesMap);

        // Update the user's location to the specified latitude and longitude
        Map<String, Object> userLocation = new HashMap<>();
        userLocation.put("latitude", latitude);
        userLocation.put("longitude", longitude);

        db.collection("users").document("1d141a0fd4e29d60").update("location", userLocation);
    }

    private boolean checkMarkerPlacedAtLocation(List<Marker> markers, double latitude, double longitude) {
        for (Marker marker : markers) {
            LatLng position = marker.getPosition();
            if (position.latitude == latitude && position.longitude == longitude) {
                return true;
            }
        }
        return false;
    }


}

