package com.example.qrcodereader;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.profile.ProfileActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for ProfileActivity.
 */
@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    /**
     * Sets up the Firestore collections and launches the ProfileActivity for testing.
     */
    @Before
    public void setUp() {
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");
        ActivityScenario.launch(ProfileActivity.class);
    }


    /**
     * Tests if the UI elements are displayed in the ProfileActivity.
     */
    @Test
    public void testUIElementsAreDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.phone))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.regionSelector))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.user_profile_photo))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    /**
     * Tests if the UI elements are displayed in the ProfileActivity.
     */
    @Test
    public void testButtonsAreClickable() {
        Espresso.onView(ViewMatchers.withId(R.id.EditButton))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }
}
