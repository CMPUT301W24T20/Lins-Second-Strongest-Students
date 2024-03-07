package com.example.qrcodereader;


import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

@LargeTest
public class CameraFragmentTest {
    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(CameraFragment.class);
    }

    @Test
    public void testScanButtonOnClick() {
        // Perform a click on the scan button
        Espresso.onView(ViewMatchers.withId(R.id.scan_button))
                .perform(ViewActions.click());


    }
}

