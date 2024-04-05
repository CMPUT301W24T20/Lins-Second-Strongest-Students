package com.example.qrcodereader;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.eventPage.EventDetailsAttendeeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    @Before
    public void setUp() {
        // Set the Firestore collections to test versions
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("vtLdBOt2ujnXybkviXg9");
    }

    @Test
    public void whenButtonIsPressed_VariableShouldChange() {

        try (ActivityScenario<EventDetailsAttendeeActivity> scenario = ActivityScenario.launch(EventDetailsAttendeeActivity.class)) {
            // Rest of your test code...

            // Use Espresso to find the button and click it
            onView(withId(R.id.sign_up_button)).perform(click());

            // Now you can retrieve the activity and check the variable's state
            // This assumes that your activity has a method to get the variable
            scenario.onActivity(activity -> {
                assertNotNull("Boolean should be true after clicking the sign-up button", activity.getDb());
                assertNotNull("Boolean should be true after clicking the sign-up button", activity.getDocRefEvent());
                assertTrue("Boolean should be true after clicking the sign-up button", activity.getSuccess());
            });
        }
    }
}

