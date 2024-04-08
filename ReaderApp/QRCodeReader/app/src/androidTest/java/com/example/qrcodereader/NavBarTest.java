package com.example.qrcodereader;


import android.content.ComponentName;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import com.example.qrcodereader.ui.eventPage.AttendeeEventActivity;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;
import com.example.qrcodereader.ui.notifications.NotificationsActivity;
import com.example.qrcodereader.ui.profile.ProfileActivity;

@RunWith(AndroidJUnit4.class)
public class NavBarTest {

    @Rule
    public ActivityScenarioRule<AttendeeEventActivity> activityRule = new ActivityScenarioRule<>(AttendeeEventActivity.class);

    @Test
    public void testTextViewButtonClick() {
        // Click on each button and verify if the corresponding activity is started
        clickButton(R.id.home_button, AttendeeEventActivity.class);
        clickButton(R.id.event_button, OrganizerEventActivity.class);
        clickButton(R.id.scanner_button, CameraActivity.class);
        clickButton(R.id.notification_button, NotificationsActivity.class);
        clickButton(R.id.bottom_profile_icon, ProfileActivity.class);

        // Assertions to verify if each button is displayed
        Espresso.onView(withId(R.id.home_button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.event_button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.scanner_button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.notification_button)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.bottom_profile_icon)).check(matches(isDisplayed()));
    }


    private void clickButton(int viewId, Class<?> expectedActivity) {
        ActivityScenario<AttendeeEventActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Click the button
            Espresso.onView(ViewMatchers.withId(viewId)).perform(ViewActions.click());

            // Verify if the expected activity is started
            if (expectedActivity != null) {
                Intent expectedIntent = new Intent(activity, expectedActivity);
                expectedIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                scenario.onActivity(activity1 -> {
                    // Check if the started activity is the expected one
                    Intent actualIntent = activity1.getIntent();
                    if (actualIntent != null) {
                        ComponentName componentName = actualIntent.getComponent();
                        if (componentName != null) {
                            String actualClassName = componentName.getClassName();
                            try {
                                Class<?> actualActivity = Class.forName(actualClassName);
                                if (expectedActivity.equals(actualActivity)) {
                                    // Activity started correctly
                                    assert true;
                                } else {
                                    // Activity started incorrectly
                                    assert false;
                                }
                            } catch (ClassNotFoundException e) {
                                // Class not found
                                assert false;
                            }
                        } else {
                            // ComponentName is null
                            assert false;
                        }
                    } else {
                        // No intent available, activity not started
                        assert false;
                    }

                });
            }
        });
    }
}
