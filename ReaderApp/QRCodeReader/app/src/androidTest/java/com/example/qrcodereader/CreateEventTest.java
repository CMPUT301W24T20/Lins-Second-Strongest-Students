package com.example.qrcodereader;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.Manifest;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.Event;
import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.eventPage.CreateEventActivity;
import com.example.qrcodereader.util.AppDataHolder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class CreateEventTest {
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.INTERNET);

    @Before
    public void setUp() {
        // Set the Firestore collections to test versions
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setQrCodeCollection("QRCodesTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");
    }

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

    @Test
    public void createInvalidEventTestWithAttendeeLimitOverflow() {
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

                EditText eventName = activity.findViewById(R.id.event_name);
                eventName.setText("Test Event");

                EditText attendeeLimit = activity.findViewById(R.id.attendee_limit);
                attendeeLimit.setText("1000000000000000000000000000000000000000000");

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

    @Test
    public void createInvalidEventTestWithAttendeeLimitNegativeOne() {
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

                EditText eventName = activity.findViewById(R.id.event_name);
                eventName.setText("Test Event");

                EditText attendeeLimit = activity.findViewById(R.id.attendee_limit);
                attendeeLimit.setText("-1");

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

    @Test
    public void createInvalidEventTestWithAttendeeLimitNegativeNumber() {
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

                EditText eventName = activity.findViewById(R.id.event_name);
                eventName.setText("Test Event");

                EditText attendeeLimit = activity.findViewById(R.id.attendee_limit);
                attendeeLimit.setText("-5");

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

    @Test
    public void createValidEventTest() {
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

                EditText eventName = activity.findViewById(R.id.event_name);
                eventName.setText("createValidEventTest");

                assert (eventDate.getText().toString().equals("2024-05-15"));
                assert (eventTime.getText().toString().equals("14:30"));
                assert (eventLocation.getText().toString().equals("Test Location"));
                assert (eventName.getText().toString().equals("createValidEventTest"));

                activity.testCreateEventDateTimeLocationInjection(2024, 5, 15, 14, 30, 0.0, 0.0, "Test Location");

                // Click the create event button
                TextView createEventButton = activity.findViewById(R.id.create_button);
                createEventButton.performClick();

                // check if the activity is still displayed
                assertNotNull(activity.findViewById(R.id.event_name));

                // Wait for Firestore operation to complete
                try {
                    Thread.sleep(3000); // This is a simple way to wait, but not recommended for real tests due to unreliability
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                FirestoreManager.getInstance().getEventCollection() // Ensure this points to your test collection
                        .whereEqualTo("name", "createValidEventTest")
                        .get()
                        .addOnCompleteListener(task -> {
                            boolean foundEvent = false;
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String eventNameResult = document.getString("name");
                                    // Now check the specific fields you expect to be correct
                                    if ("createValidEventTest".equals(eventNameResult)) {
                                        foundEvent = true;
                                        // Perform additional checks as needed, e
                                        assertEquals(FirestoreManager.getInstance().getUserID(), document.getString("organizerID"));
                                        assertEquals("Test Location", document.getString("locationName"));
                                        assertEquals(Long.valueOf(-1), document.getLong("attendeeLimit"));
                                        break;
                                    }
                                }
                            }
                            assertTrue("Event was not found or didn't match the expected values", foundEvent);

                            // Clean up - delete the event and related QR codes after assertion
                            if (foundEvent) {
                                String documentId = task.getResult().getDocuments().get(0).getId();
                                deleteEventAndRelatedQRCodes(documentId);
                            }
                        });


                deleteEventAndRelatedQRCodes("createValidEventTest");
            });
        }
    }

    @Test
    public void createEventWithAttendeeLimit() {
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

                EditText eventName = activity.findViewById(R.id.event_name);
                eventName.setText("createEventWithAttendeeLimit");

                EditText attendeeLimit = activity.findViewById(R.id.attendee_limit);
                attendeeLimit.setText("5");

                assert (eventDate.getText().toString().equals("2024-05-15"));
                assert (eventTime.getText().toString().equals("14:30"));
                assert (eventLocation.getText().toString().equals("Test Location"));
                assert (eventName.getText().toString().equals("createEventWithAttendeeLimit"));
                assert (attendeeLimit.getText().toString().equals("5"));

                activity.testCreateEventDateTimeLocationInjection(2024, 5, 15, 14, 30, 0.0, 0.0, "Test Location");

                // Click the create event button
                TextView createEventButton = activity.findViewById(R.id.create_button);
                createEventButton.performClick();

                // check if the activity is still displayed
                assertNotNull(activity.findViewById(R.id.event_name));

                // Wait for Firestore operation to complete
                try {
                    Thread.sleep(3000); // This is a simple way to wait, but not recommended for real tests due to unreliability
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                FirestoreManager.getInstance().getEventCollection() // Ensure this points to your test collection
                        .whereEqualTo("name", "createEventWithAttendeeLimit")
                        .get()
                        .addOnCompleteListener(task -> {
                            boolean foundEvent = false;
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String eventNameResult = document.getString("name");
                                    // Now check the specific fields you expect to be correct
                                    if ("createEventWithAttendeeLimit".equals(eventNameResult)) {
                                        foundEvent = true;
                                        // Perform additional checks as needed, e
                                        assertEquals(FirestoreManager.getInstance().getUserID(), document.getString("organizerID"));
                                        assertEquals("Test Location", document.getString("locationName"));
                                        assertEquals(Long.valueOf(5), document.getLong("attendeeLimit"));
                                        // Add more assertions as needed
                                        break;
                                    }
                                }
                            }
                            assertTrue("Event was not found or didn't match the expected values", foundEvent);

                            // Clean up - delete the event and related QR codes after assertion
                            if (foundEvent) {
                                String documentId = task.getResult().getDocuments().get(0).getId();
                                deleteEventAndRelatedQRCodes(documentId);
                            }
                        });

                deleteEventAndRelatedQRCodes("createEventWithAttendeeLimit");
            });
        }
    }

    @Test
    public void TestReuseQRButtonWhenThereIsNoEvent() {
        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.reuse_QR_button))
                    .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));

        }
    }

    @Test
    public void TestReuseQRButtonWhenThereIsEvent() {
        AppDataHolder appDataHolder = AppDataHolder.getInstance();

        // Create a dummy event
        Event event = new Event("1", "Test Event", "Test Organizer", null, null, "Test Poster");
        ArrayList<Event> eventDataList = new ArrayList<>();
        eventDataList.add(event);
        appDataHolder.setPastEvents(eventDataList);

        try (ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(CreateEventActivity.class)) {
            onView(withId(R.id.reuse_QR_button))
                    .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
    }

    private void deleteEventAndRelatedQRCodes(String eventName) {
        FirebaseFirestore db = FirestoreManager.getInstance().getDb();
        CollectionReference eventsRef = FirestoreManager.getInstance().getEventCollection();
        CollectionReference qrCodesRef = FirestoreManager.getInstance().getQrCodeCollection(); // Assuming you have a method to get this

        // First, find the event by name
        eventsRef.whereEqualTo("name", eventName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot eventDocument : task.getResult()) {
                            String eventId = eventDocument.getId();

                            // Delete the event document
                            eventsRef.document(eventId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Test", "Event successfully deleted"))
                                    .addOnFailureListener(e -> Log.w("Test", "Error deleting event", e));

                            // Now, query and delete related QR codes
                            qrCodesRef.whereEqualTo("eventID", eventId)
                                    .get()
                                    .addOnCompleteListener(qrTask -> {
                                        if (qrTask.isSuccessful() && !qrTask.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot qrDocument : qrTask.getResult()) {
                                                String qrDocumentId = qrDocument.getId();

                                                // Delete each related QR code document
                                                qrCodesRef.document(qrDocumentId)
                                                        .delete()
                                                        .addOnSuccessListener(aVoid -> Log.d("Test", "QR code successfully deleted"))
                                                        .addOnFailureListener(e -> Log.w("Test", "Error deleting QR code", e));
                                            }
                                        } else {
                                            Log.w("Test", "Error querying QR code documents: ", qrTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.w("Test", "Error querying event documents: ", task.getException());
                    }
                });
    }

}
