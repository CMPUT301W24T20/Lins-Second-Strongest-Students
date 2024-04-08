package com.example.qrcodereader;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.profile.ProfileActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for the ProfilePictureFrag fragment in ProfileActivity.
 * Note: This test is currently uncertain, it's unknown if the test or the fragment itself is broken.
 */
@RunWith(AndroidJUnit4.class)
public class ProfilePictureFragTest {

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
     * Tests if the UI elements are displayed in the ProfilePictureFrag fragment.
     */
    @Test
    public void testUIElementsAreDisplayed() {
        // Click the edit button to launch the ProfileEditFrag
        Espresso.onView(ViewMatchers.withId(R.id.EditButton))
                .perform(ViewActions.click());

        // Click the profile picture button to launch the ProfilePictureFrag
        Espresso.onView(ViewMatchers.withId(R.id.ProfilePic))
                .perform(ViewActions.click());

        // Now check if the UI elements in ProfilePictureFrag are displayed
        Espresso.onView(ViewMatchers.withId(R.id.UploadProfile))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.RemoveProfile))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    /**
     * Tests if the buttons are clickable in the ProfilePictureFrag fragment.
     */
    @Test
    public void testButtonsAreClickable() {
        // Click the edit button to launch the ProfileEditFrag
        Espresso.onView(ViewMatchers.withId(R.id.EditButton))
                .perform(ViewActions.click());

        // Click the profile picture button to launch the ProfilePictureFrag
        Espresso.onView(ViewMatchers.withId(R.id.ProfilePic))
                .perform(ViewActions.click());

        // Now check if the buttons in ProfilePictureFrag are clickable
        Espresso.onView(ViewMatchers.withId(R.id.UploadProfile))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
        Espresso.onView(ViewMatchers.withId(R.id.RemoveProfile))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }
}

