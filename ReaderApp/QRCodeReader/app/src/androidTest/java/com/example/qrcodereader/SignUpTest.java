package com.example.qrcodereader;

import static androidx.test.espresso.Espresso.onView;
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
import com.example.qrcodereader.ui.eventPage.EventDetailsAttendeeActivity;
import com.example.qrcodereader.ui.eventPage.EventRemoveAttendeeActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

/**
 * Test class for signing up and removing an attendee from an event.
 */
@RunWith(AndroidJUnit4.class)
public class SignUpTest {

    /**
     * Grants the necessary permission for the tests and sets up the Firestore collections.
     */
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    /**
     * Sets up the Firestore collections for testing.
     */
    @Before
    public void setUp() {
        // Set the Firestore collections to test versions
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");
    }

    /**
     * Tests signing up for an event and then removing the attendee from the event.
     */
    @Test
    public void SignUpAndRemoveTest() {

        try (ActivityScenario<EventDetailsAttendeeActivity> scenario = ActivityScenario.launch(EventDetailsAttendeeActivity.class)) {
            // Rest of your test code...
            // Wait for 3 seconds after the activity has launched
            Thread.sleep(5000);

            // Use Espresso to find the button and click it
            onView(withId(R.id.sign_up_button)).perform(click());

            Thread.sleep(3000);

            // Now you can retrieve the activity and check the variable's state
            // This assumes that your activity has a method to get the variable
            FirestoreManager.getInstance().getEventDocRef().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Extract the map field from the document
                        Map<String, Object> map = (Map<String, Object>) document.getData().get("attendees");
                        assertNotNull(map);
                        assertTrue(map.containsKey("1d141a0fd4e29d60"));
                    } else {
                        fail();
                    }
                }
            });
            FirestoreManager.getInstance().getUserDocRef().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Extract the map field from the document
                        Map<String, Object> map = (Map<String, Object>) document.getData().get("eventsAttended");
                        assertNotNull(map);
                        assertTrue(map.containsKey("6NRHwbgGk0449AVOBPLs"));
                    } else {
                        fail();
                    }
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try (ActivityScenario<EventRemoveAttendeeActivity> scenario = ActivityScenario.launch(EventRemoveAttendeeActivity.class)) {
            // Rest of your test code...
            // Wait for 3 seconds after the activity has launched
            Thread.sleep(3000);

            // Use Espresso to find the button and click it
            onView(withId(R.id.sign_up_button)).perform(click());

            Thread.sleep(3000);
            // Now you can retrieve the activity and check the variable's state
            // This assumes that your activity has a method to get the variable
            FirestoreManager.getInstance().getEventDocRef().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Extract the map field from the document
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Map<String, Object> map = (Map<String, Object>) document.getData().get("attendees");
                        assertNotNull(map);
                        assertFalse(map.containsKey("1d141a0fd4e29d60"));
                    } else {
                        fail();
                    }
                }
            });
            FirestoreManager.getInstance().getUserDocRef().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        // Extract the map field from the document
                        Map<String, Object> map = (Map<String, Object>) document.getData().get("eventsAttended");
                        assertNotNull(map);
                        assertFalse(map.containsKey("6NRHwbgGk0449AVOBPLs"));
                    } else {
                        fail();
                    }
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}