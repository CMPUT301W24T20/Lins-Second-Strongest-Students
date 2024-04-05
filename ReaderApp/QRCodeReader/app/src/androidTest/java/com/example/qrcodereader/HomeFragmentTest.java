package com.example.qrcodereader;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcodereader.ui.home.HomeFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


//THIS WORKS IF THIS FAILS U BROKE SOMETHING
@RunWith(AndroidJUnit4.class)
public class HomeFragmentTest {

    @Before
    public void setUp() {
        FragmentScenario<HomeFragment> scenario = FragmentScenario.launchInContainer(HomeFragment.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    @Test
    public void testProfileButtonIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.profile_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testMyEventButtonIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.my_event_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testAdminButtonIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.admin_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
