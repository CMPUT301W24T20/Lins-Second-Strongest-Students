package com.example.qrcodereader;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.eventPage.EventDetailsOrganizerActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test class for removing an event as an organizer in EventDetailsOrganizerActivity.
 */
@RunWith(AndroidJUnit4.class)
public class RemoveEventOrganizerTest {
    /**
     * Grants the necessary permission for the tests and sets up the Firestore collections.
     */
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    /**
     * Sets up the Firestore collections and adds a test event and user for testing.
     */
    @Before
    public void setUp() {
        // Set the Firestore collections to test versions
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("12ZMK89JwC5tWRxQWyXh");

        Map<String, Object> event = new HashMap<>();
        event.put("attendeeLimit", -1);

        Map<String, Integer> attendees = new HashMap<>();
        attendees.put("1d141a0fd4e29d60", 1);  // This would be a map field with user ID as key and a value.
        event.put("attendees", attendees);

        event.put("location", new GeoPoint(0.0, 0.0)); // For GeoPoint, you need to use the GeoPoint class.
        event.put("locationName", "Space");
        event.put("name", "TestEvent2");
        event.put("organizer", ""); // Assuming this is a String.
        event.put("organizerID", "1d141a0fd4e29d60");
        event.put("qrCode", "1FOM%dx7r91n2+kObwz#MRR#RY*aOTt");
        event.put("time", new Timestamp(new Date())); // For timestamp, you use a Timestamp object.

        FirestoreManager.getInstance().getEventCollection().document("12ZMK89JwC5tWRxQWyXh")
                .set(event)
                .addOnSuccessListener(documentReference -> {
                    assertTrue("Successfully added event", true);
                })
                .addOnFailureListener(e -> {
                    fail("Failed to add event");
                });

        Map<String, Object> updates = new HashMap<>();
        updates.put("eventsAttended." + "12ZMK89JwC5tWRxQWyXh", 0);

        FirestoreManager.getInstance().getUserDocRef().update(updates)
                .addOnSuccessListener(documentReference -> {
                    assertTrue("Successfully added event to user", true);
                })
                .addOnFailureListener(e -> {
                    fail("Failed to add event to user");
                });
    }

    /**
     * Tests the removal of an event by an organizer and verifies that the event is deleted from the database
     * and from all users' eventsAttended lists.
     */
    @Test
    public void RemoveOrganizerEventTest() {



        try (ActivityScenario<EventDetailsOrganizerActivity> scenario = ActivityScenario.launch(EventDetailsOrganizerActivity.class)) {
            // Wait for 5 seconds after the activity has launched
            Thread.sleep(5000);

            scenario.onActivity(activity -> {
                activity.removeEvent(FirestoreManager.getInstance().getEventID(), FirestoreManager.getInstance().getEventCollection(), FirestoreManager.getInstance().getUserCollection());

            });

            Thread.sleep(3000);

            // Now you can retrieve the activity and check the variable's state
            // This assumes that your activity has a method to get the variable
            FirestoreManager.getInstance().getEventDocRef().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fail();
                    } else {
                        assertTrue("Successfully deleted event", true);
                    }
                }
            });

            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicBoolean allUsersChecked = new AtomicBoolean(true); // A flag to track the result

            FirestoreManager.getInstance().getUserCollection().get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Boolean> eventsAttended = (Map<String, Boolean>) document.get("eventsAttended");
                                // Check if the map is not null and contains the event ID
                                if (eventsAttended != null && eventsAttended.containsKey("12ZMK89JwC5tWRxQWyXh")) {
                                    allUsersChecked.set(false);
                                    break;
                                }
                            }
                        } else {
                            allUsersChecked.set(false); // Set to false if the task was not successful
                        }
                        latch.countDown(); // Signal that we've checked all documents
                    });
            assertTrue("Latch should have counted down", latch.await(5, TimeUnit.SECONDS));
            assertTrue("Every user's eventsAttended should not contain the event ID", allUsersChecked.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
