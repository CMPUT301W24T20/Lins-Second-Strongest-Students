package com.example.qrcodereader;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static org.hamcrest.Matchers.anything;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.EventDetailsOrganizerActivity;
import com.example.qrcodereader.ui.eventPage.EventRemoveAttendeeActivity;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class AttendeeActivityTest {

    @Before
    public void setUp() {
        // Set the Firestore collections to test versions
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");

        Map<String, Object> updates = new HashMap<>();
        updates.put("eventsAttended." + "6NRHwbgGk0449AVOBPLs", 0);

        FirestoreManager.getInstance().getUserDocRef().update(updates)
                .addOnSuccessListener(documentReference -> {
                    assertTrue("Successfully added event to user", true);
                })
                .addOnFailureListener(e -> {
                    fail("Failed to add event to user");
                });
    }
    @Test
    public void testListViewAndLinearLayoutPresence() {
        // Start the activity
        ActivityScenario.launch(AttendeeEventActivity.class);

        // Check if LinearLayout is present
        onView(withId(R.id.nav_bar))
                .check(matches(isDisplayed()));

        // Check if ListView is present
        onView(withId(R.id.event_list_attendee))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testListViewItemClickNavigatesToAnotherActivity() {
        // Initialize Intents and start the activity
        androidx.test.espresso.intent.Intents.init();
        ActivityScenario.launch(AttendeeEventActivity.class);

        // Perform click on the first item in the ListView
        // Note: You might need to replace with a specific child matcher if your ListView items are custom
        onData(anything())
                .inAdapterView(withId(R.id.event_list_attendee))
                .atPosition(0)
                .perform(click());

        // Check if the intended Activity is opened
        intended(hasComponent(EventRemoveAttendeeActivity.class.getName()));

        // Release Intents
        androidx.test.espresso.intent.Intents.release();
    }
}
