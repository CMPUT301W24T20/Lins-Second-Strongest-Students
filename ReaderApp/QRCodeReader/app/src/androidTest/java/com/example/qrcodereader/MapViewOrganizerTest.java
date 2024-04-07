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

@RunWith(AndroidJUnit4.class)
public class MapViewOrganizerTest {
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testMarkerAddedToMap() throws InterruptedException {
        try (ActivityScenario<MapViewOrganizer> scenario = ActivityScenario.launch(MapViewOrganizer.class)) {
            scenario.onActivity(activity -> {
                // Wait for the activity to initialize and the map to be ready
                try {
                    Thread.sleep(10000); // Increase the delay
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Add a test marker
                activity.addTestMarker(37.422, -122.084);

                // Wait for the marker to be added
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Check if the markers list is not empty
                List<Marker> markers = activity.getMarkers();
                Log.d("MapViewOrganizerTest", "Markers list size: " + markers.size());
                assertFalse("Markers list should not be empty", markers.isEmpty());
            });
        }
    }
}
