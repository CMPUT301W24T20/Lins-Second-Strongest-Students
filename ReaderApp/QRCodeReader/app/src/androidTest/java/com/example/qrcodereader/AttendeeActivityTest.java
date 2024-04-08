package com.example.qrcodereader;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.BrowseEventActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for AttendeeEventActivity.
 */
@RunWith(AndroidJUnit4.class)
public class AttendeeActivityTest {
    /**
     * Grants location permissions needed for the tests.
     */
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION);
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
     * Tests the presence of the navigation bar and the event list in AttendeeEventActivity.
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    @Test
    public void testListViewAndLinearLayoutPresence() throws InterruptedException {
        // Start the activity
        try (ActivityScenario<AttendeeEventActivity> scenario = ActivityScenario.launch(AttendeeEventActivity.class)) {

            Thread.sleep(5000);

            onView(withText("CANCEL")).perform(click());

            // Check if LinearLayout is present
            onView(withId(R.id.nav_bar))
                    .check(matches(isDisplayed()));

            // Check if ListView is present
            onView(withId(R.id.event_list_attendee))
                    .check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Tests if clicking on a TextView navigates to the BrowseEventActivity.
     *
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    @Test
    public void testTextViewClickNavigatesToAnotherActivity() throws InterruptedException {
        // Initialize Intents and start the activity
        androidx.test.espresso.intent.Intents.init();
        try (ActivityScenario<AttendeeEventActivity> scenario = ActivityScenario.launch(AttendeeEventActivity.class)) {

            Thread.sleep(5000);

            onView(withText("CANCEL")).perform(click());

            Thread.sleep(5000);

            // Perform click on the first item in the ListView
            // Note: You might need to replace with a specific child matcher if your ListView items are custom
            onView(withId(R.id.browse_button)).perform(click());

            // Check if the intended Activity is opened
            intended(hasComponent(BrowseEventActivity.class.getName()));

            // Release Intents
            androidx.test.espresso.intent.Intents.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
