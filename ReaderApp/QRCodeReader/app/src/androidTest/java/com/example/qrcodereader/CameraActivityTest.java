package com.example.qrcodereader;


import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.qrcodereader.ui.camera.CameraActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * Test class for CameraActivity.
 */
@RunWith(AndroidJUnit4.class)

@LargeTest
public class CameraActivityTest {
    /**
     * Launches the CameraActivity before each test.
     */
    @Before
    public void setUp() {
        ActivityScenario.launch(CameraActivity.class);
    }
    /**
     * Tests the behavior of the scan button when clicked.
     */
    @Test
    public void testScanButtonOnClick() {
        // Perform a click on the scan button
        Espresso.onView(ViewMatchers.withId(R.id.scan_button))
                .perform(ViewActions.click());


    }
}

