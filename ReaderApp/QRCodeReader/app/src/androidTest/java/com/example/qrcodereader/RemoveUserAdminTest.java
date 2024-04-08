package com.example.qrcodereader;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.admin.UserDetailsAdminActivity;
import com.example.qrcodereader.ui.eventPage.UserDetailsOrganizerActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;



import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
public class RemoveUserAdminTest {
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    @Before
    public void setUp() {
        // Set the Firestore collections to test versions
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("6a38dbd30b66b3cd");
        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");

        Map<String, Object> user = new HashMap<>();
        user.put("ProfilePic", "https://firebasestorage.googleapis.com/v0/b/linssecondstrongeststudents.appspot.com/o/Profiles%2F837.png?alt=media&token=be882173-2bc8-4b16-b677-77f7d9dd66fb");

        Map<String, Integer> eventsAttended = new HashMap<>();
        eventsAttended.put("6NRHwbgGk0449AVOBPLs", 1);  // This would be a map field with user ID as key and a value.
        user.put("eventsAttended", eventsAttended);

        user.put("location", new GeoPoint(0.0, 0.0)); // For GeoPoint, you need to use the GeoPoint class.
        user.put("name", "TestUser2");
        user.put("phone", "");
        user.put("phoneRegion", "");

        FirestoreManager.getInstance().getUserCollection().document("6a38dbd30b66b3cd")
                .set(user)
                .addOnSuccessListener(documentReference -> {
                    assertTrue("Successfully added user", true);
                })
                .addOnFailureListener(e -> {
                    fail("Failed to add user");
                });

        Map<String, Object> updates = new HashMap<>();
        updates.put("attendees." + "6a38dbd30b66b3cd", 0);

        FirestoreManager.getInstance().getEventDocRef().update(updates)
                .addOnSuccessListener(documentReference -> {
                    assertTrue("Successfully added event to user", true);
                })
                .addOnFailureListener(e -> {
                    fail("Failed to add event to user");
                });
    }

    @Test
    public void RemoveAdminUserTest() {



        try (ActivityScenario<UserDetailsAdminActivity> scenario = ActivityScenario.launch(UserDetailsAdminActivity.class)) {
            // Rest of your test code...
            // Wait for 5 seconds after the activity has launched
            Thread.sleep(5000);

            scenario.onActivity(activity -> {
                activity.removeUser(FirestoreManager.getInstance().getUserID(), FirestoreManager.getInstance().getUserCollection(), FirestoreManager.getInstance().getEventCollection());

            });

            Thread.sleep(3000);

            // Now you can retrieve the activity and check the variable's state
            // This assumes that your activity has a method to get the variable
            FirestoreManager.getInstance().getUserDocRef().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        fail();
                    } else {
                        assertTrue("Successfully deleted user", true);
                    }
                }
            });

            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicBoolean allEventsChecked = new AtomicBoolean(true); // A flag to track the result

            FirestoreManager.getInstance().getEventCollection().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Boolean> attendees = (Map<String, Boolean>) document.get("attendees");
                        // Check if the map is not null and contains the user ID
                        if (attendees != null && attendees.containsKey("12ZMK89JwC5tWRxQWyXh")) {
                            allEventsChecked.set(false);
                            break;
                        }
                    }
                } else {
                    allEventsChecked.set(false); // Set to false if the task was not successful
                }
                latch.countDown(); // Signal that we've checked all documents
            });
            assertTrue("Latch should have counted down", latch.await(5, TimeUnit.SECONDS));
            assertTrue("Every event's attendees should not contain the user ID", allEventsChecked.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
