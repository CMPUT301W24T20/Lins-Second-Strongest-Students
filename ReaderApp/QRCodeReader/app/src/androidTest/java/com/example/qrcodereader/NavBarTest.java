package com.example.qrcodereader;



import androidx.test.core.app.ActivityScenario;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.profile.ProfileActivity;

@RunWith(AndroidJUnit4.class)
public class NavBarTest {

    @Before
    public void setUp(){
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");
        ActivityScenario.launch(ProfileActivity.class);
    }

    @Test
    public void testHomeButtonIsDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.home_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testHomeButtonIsClickable(){
        Espresso.onView(ViewMatchers.withId(R.id.home_button))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void testEventButtonIsDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.event_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testEventButtonIsClickable(){
        Espresso.onView(ViewMatchers.withId(R.id.event_button))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void testScannerButtonIsDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.scanner_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testScannerButtonIsClickable(){
        Espresso.onView(ViewMatchers.withId(R.id.scanner_button))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void testNotificationButtonIsDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.notification_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNotificationButtonIsClickable(){
        Espresso.onView(ViewMatchers.withId(R.id.notification_button))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void testProfileButtonIsDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.bottom_profile_icon))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testProfileButtonIsClickable(){
        Espresso.onView(ViewMatchers.withId(R.id.bottom_profile_icon))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }


}
