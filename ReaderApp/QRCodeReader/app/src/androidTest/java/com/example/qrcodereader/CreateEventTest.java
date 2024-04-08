package com.example.qrcodereader;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FileUtils.waitFor;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static java.util.regex.Pattern.matches;

import android.Manifest;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.eventPage.CreateEventActivity;
import com.example.qrcodereader.ui.eventPage.EventDetailsAttendeeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.security.cert.PKIXParameters;
/**
 * Test class for CreateEventActivity.
 */
public class CreateEventTest {
    /**
     * Grants the necessary permission for the tests.
     */
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.INTERNET);

    /**
     * Sets up the Firestore collections and documents for testing.
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
     * Tests if the CreateEventActivity is launched successfully with all required fields present.
     */
    @Test
    public void testCreateEventLaunch() {
        // Prepare the intent with extras
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            // Check if the activity is launched
            scenario.onActivity(activity -> {
                // Check if the QR code is displayed
                assertNotNull(activity.findViewById(R.id.event_name));
                assertNotNull(activity.findViewById(R.id.event_date));
                assertNotNull(activity.findViewById(R.id.event_time));
                assertNotNull(activity.findViewById(R.id.event_location));
                assertNotNull(activity.findViewById(R.id.attendee_limit));
            });
        }
    }

    /**
     * Tests creating an event with a missing location field.
     */
    @Test
    public void createInvalidEventTestWithMissingLocation() {
        // Launch the Activity
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            scenario.onActivity(activity -> {
                // Check if the QR code is displayed
                EditText eventDate = activity.findViewById(R.id.event_date);
                eventDate.setText("2024-05-15");

                EditText eventTime = activity.findViewById(R.id.event_time);
                eventTime.setText("14:30");

                EditText eventName = activity.findViewById(R.id.event_name);
                eventName.setText("Test Event");

                assert (eventDate.getText().toString().equals("2024-05-15"));
                assert (eventTime.getText().toString().equals("14:30"));
                assert (eventName.getText().toString().equals("Test Event"));

                // Click the create event button
                TextView createEventButton = activity.findViewById(R.id.create_button);
                createEventButton.performClick();

                // check if the activity is still displayed
                assertNotNull(activity.findViewById(R.id.event_name));
            });
        }
    }

    /**
     * Tests creating an event with a missing name field.
     */
    @Test
    public void createInvalidEventTestWithMissingName() {
        // Launch the Activity
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            scenario.onActivity(activity -> {
                // Check if the QR code is displayed
                EditText eventDate = activity.findViewById(R.id.event_date);
                eventDate.setText("2024-05-15");

                EditText eventTime = activity.findViewById(R.id.event_time);
                eventTime.setText("14:30");

                EditText eventLocation = activity.findViewById(R.id.event_location);
                eventLocation.setText("Test Location");

                assert (eventDate.getText().toString().equals("2024-05-15"));
                assert (eventTime.getText().toString().equals("14:30"));
                assert (eventLocation.getText().toString().equals("Test Location"));

                // Click the create event button
                TextView createEventButton = activity.findViewById(R.id.create_button);
                createEventButton.performClick();

                // check if the activity is still displayed
                assertNotNull(activity.findViewById(R.id.event_name));
            });
        }
    }

    /**
     * Tests creating an event with a missing date field.
     */
    @Test
    public void createInvalidEventTestWithMissingDate() {
        // Launch the Activity
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            scenario.onActivity(activity -> {
                // Check if the QR code is displayed
                EditText eventTime = activity.findViewById(R.id.event_time);
                eventTime.setText("14:30");

                EditText eventLocation = activity.findViewById(R.id.event_location);
                eventLocation.setText("Test Location");

                EditText eventName = activity.findViewById(R.id.event_name);
                eventName.setText("Test Event");

                assert (eventTime.getText().toString().equals("14:30"));
                assert (eventLocation.getText().toString().equals("Test Location"));
                assert (eventName.getText().toString().equals("Test Event"));

                // Click the create event button
                TextView createEventButton = activity.findViewById(R.id.create_button);
                createEventButton.performClick();

                // check if the activity is still displayed
                assertNotNull(activity.findViewById(R.id.event_name));
            });
        }
    }

    /**
     * Tests creating an event with a missing time field.
     */
    @Test
    public void createInvalidEventTestWithMissingTime() {
        // Launch the Activity
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            scenario.onActivity(activity -> {
                // Check if the QR code is displayed
                EditText eventDate = activity.findViewById(R.id.event_date);
                eventDate.setText("2024-05-15");

                EditText eventLocation = activity.findViewById(R.id.event_location);
                eventLocation.setText("Test Location");

                EditText eventName = activity.findViewById(R.id.event_name);
                eventName.setText("Test Event");

                assert (eventDate.getText().toString().equals("2024-05-15"));
                assert (eventLocation.getText().toString().equals("Test Location"));
                assert (eventName.getText().toString().equals("Test Event"));

                // Click the create event button
                TextView createEventButton = activity.findViewById(R.id.create_button);
                createEventButton.performClick();

                // check if the activity is still displayed
                assertNotNull(activity.findViewById(R.id.event_name));
            });
        }
    }


}
