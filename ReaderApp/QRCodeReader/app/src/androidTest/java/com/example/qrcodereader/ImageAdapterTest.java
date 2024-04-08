package com.example.qrcodereader;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.example.qrcodereader.R;
import com.example.qrcodereader.ui.admin.TestActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class ImageAdapterTest {

    @Test
    public void testSelection() {
        ActivityScenario.launch(TestActivity.class);

        // Perform a click on the first item in the adapter
        onView(withId(R.id.recyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click())
        );

        // Check if the first item has a blue overlay
        onView(withTagValue(is(0))).check(
                matches(ViewMatchers.hasBackground(R.color.sky_blue_translucent))
        );
    }


}
