package com.example.qrcodereader;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.qrcodereader.ui.profile.ProfileActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


//Broken
@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    @Rule
    public ActivityTestRule<ProfileActivity> activityRule =
            new ActivityTestRule<>(ProfileActivity.class);

    @Test
    public void testUIElementsAreDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.name))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.phone))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.regionSelector))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.user_profile_photo))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testButtonsAreClickable() {
        Espresso.onView(ViewMatchers.withId(R.id.EditButton))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
        Espresso.onView(ViewMatchers.withId(R.id.admin_button))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }
}
