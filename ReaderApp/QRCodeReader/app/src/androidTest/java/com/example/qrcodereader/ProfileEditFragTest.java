package com.example.qrcodereader;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.qrcodereader.ui.profile.ProfileActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


//THIS WORKS IF THIS FAILS U BROKE THE APP
@RunWith(AndroidJUnit4.class)
public class ProfileEditFragTest {

    @Rule
    public ActivityTestRule<ProfileActivity> activityRule =
            new ActivityTestRule<>(ProfileActivity.class);

    @Test
    public void testUIElementsAreDisplayed() {
        // Click the edit button to launch the ProfileEditFrag
        Espresso.onView(ViewMatchers.withId(R.id.EditButton))
                .perform(ViewActions.click());

        // Now check if the UI elements in ProfileEditFrag are displayed
        Espresso.onView(ViewMatchers.withId(R.id.name))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.phone))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.SpinnerRegions))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.ProfilePic))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testButtonsAreClickable() {
        // Click the edit button to launch the ProfileEditFrag
        Espresso.onView(ViewMatchers.withId(R.id.EditButton))
                .perform(ViewActions.click());

        // Now check if the profile picture in ProfileEditFrag is clickable
        Espresso.onView(ViewMatchers.withId(R.id.ProfilePic))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }
}
