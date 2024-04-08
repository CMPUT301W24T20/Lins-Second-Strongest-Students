package com.example.qrcodereader;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;

import org.junit.After;
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
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;



import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.admin.EventDetailsAdminActivity;
import com.example.qrcodereader.ui.admin.UserDetailsAdminActivity;
import com.example.qrcodereader.ui.eventPage.BrowseEventActivity;
import com.example.qrcodereader.ui.eventPage.EventDetailsOrganizerActivity;
import com.example.qrcodereader.ui.profile.ProfileActivity;
import com.example.qrcodereader.ui.eventPage.EventRemoveAttendeeActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
public class AdminButtonTest {

    @Test
    public void AdminVisibilityTest() {
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");

        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class)) {

            Thread.sleep(5000);


            // Check if LinearLayout is present
            onView(withId(R.id.nav_bar))
                    .check(matches(isDisplayed()));

            // Check if ListView is present
            onView(withId(R.id.admin_button))
                    .check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void AdminInvisibilityTest() {
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
        user.put("phone", "AC");
        user.put("phoneRegion", "99999");

        FirestoreManager.getInstance().getUserCollection().document("6a38dbd30b66b3cd")
                .set(user)
                .addOnSuccessListener(documentReference -> {
                    assertTrue("Successfully added user", true);
                })
                .addOnFailureListener(e -> {
                    fail("Failed to add user");
                });

        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class)) {

            Thread.sleep(5000);


            // Check if LinearLayout is present
            onView(withId(R.id.nav_bar))
                    .check(matches(isDisplayed()));

            // Check if ListView is present
            onView(withId(R.id.admin_button))
                    .check(matches(not(isDisplayed())));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try (ActivityScenario<UserDetailsAdminActivity> scenario = ActivityScenario.launch(UserDetailsAdminActivity.class)) {
            // Rest of your test code...
            // Wait for 5 seconds after the activity has launched
            Thread.sleep(5000);

            scenario.onActivity(activity -> {
                activity.removeUser("6a38dbd30b66b3cd", FirestoreManager.getInstance().getUserCollection(), FirestoreManager.getInstance().getEventCollection());
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
