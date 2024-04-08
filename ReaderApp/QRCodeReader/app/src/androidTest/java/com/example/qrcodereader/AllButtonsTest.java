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
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class AllButtonsTest {

    @Rule
    public GrantPermissionRule grantLocationPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
        Intents.init(); // Initialize Espresso-Intents
    }

    @After
    public void tearDown() {
        Intents.release(); // Release Espresso-Intents
    }



//    @Test
//    public void testProfileButtonOnClick() {
//        // Launch your Activity under test
//        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
//
//        // Use Espresso to perform a click on the profile button
//        onView(withId(R.id.profile_button)).perform(click());
//
//        // Now use Espresso to check if the DialogFragment is displayed
//        onView(withText("Location Access")).check(matches(isDisplayed()));
//    }
//
//
//    @Test
//    public void testEventsButtonOnClick() {
//        // Perform a click on the events button
//        onView(withId(R.id.my_event_button)).perform(click());
//
//        // Check if a dialog is displayed
//        onView(withText("Choose Event Page")).check(matches(isDisplayed()));
//
//        // Perform a click on the "Go to Your Event Page (Attendee)" button
//        onView(withText("Go to Your Event Page (Attendee)")).perform(click());
//
//        // Verify that AttendeeEventActivity is started
//        intended(hasComponent(AttendeeEventActivity.class.getName()));
//
//        //Click the return button
//        onView(withId(R.id.return_button)).perform(click());
//
//        // Perform a click on the events button again
//        onView(withId(R.id.my_event_button)).perform(click());
//
//        // Perform a click on the "Go to Your Event Page (Organizer)" button
//        onView(withText("Go to Your Event Page (Organizer)")).perform(click());
//
//        // Verify that OrganizerEventActivity is started
//        intended(hasComponent(OrganizerEventActivity.class.getName()));
//    }



}
