package com.example.qrcodereader;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.home.HomeFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


//THIS WORKS IF THIS FAILS U BROKE SOMETHING
@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION);
    @Before
    public void setUp() {
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");
        FragmentScenario<HomeFragment> scenario = FragmentScenario.launchInContainer(HomeFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void testProfileButtonIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.profile_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @Test
    public void testProfileButtonIsClickable(){
        Espresso.onView(ViewMatchers.withId(R.id.profile_button))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }


    @Test
    public void testMyEventButtonIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.my_event_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testMyEventButtonIsClickable(){
        Espresso.onView(ViewMatchers.withId(R.id.my_event_button))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void testAdminButtonIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.admin_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testAdminButtonIsClickable(){
        Espresso.onView(ViewMatchers.withId(R.id.admin_button))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }
}

