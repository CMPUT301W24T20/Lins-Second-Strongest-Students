package com.example.qrcodereader;

import android.Manifest;


import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test class for MapView activity.
 */
public class MapViewTest {


    /**
     * Grants location permissions needed for the tests.
     */
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);

    private CountingIdlingResource idlingResource;

    /**
     * Grants location permissions needed for the tests.
     */
    @Before
    public void setUp() {
        idlingResource = new CountingIdlingResource("MapView");
        IdlingRegistry.getInstance().register(idlingResource);
    }

    /**
     * Tests if the map view is displayed in the MapView activity.
     */
    @Test
    public void testMapView() {
        ActivityScenario.launch(MapView.class);

        // Check if the map fragment is displayed
        Espresso.onView(withId(R.id.gmaps)).check(matches(isDisplayed()));
    }
}
