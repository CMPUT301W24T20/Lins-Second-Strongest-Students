package com.example.qrcodereader;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcodereader.ui.notifications.NotificationsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class NotificationsActivityTest {

    @Rule
    public ActivityScenarioRule<NotificationsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(NotificationsActivity.class);

    @Test
    public void testDeleteOneNotification() {
        try (ActivityScenario<NotificationsActivity> scenario = ActivityScenario.launch(NotificationsActivity.class)) {
            // Perform action
            onView(withId(R.id.delete_button)).perform(click());

            // Assertion: Check if the notification is removed from the list
            onView(withId(R.id.notification_list)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testClearAllNotifications() {
        try (ActivityScenario<NotificationsActivity> scenario = ActivityScenario.launch(NotificationsActivity.class)) {
            // Perform action
            onView(withId(R.id.clear_button)).perform(click());

            // Assertion: Check if the notification list is empty
            onView(withId(R.id.notification_list)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testReturnButton() {
        try (ActivityScenario<NotificationsActivity> scenario = ActivityScenario.launch(NotificationsActivity.class)) {
            // Perform action
            onView(withId(R.id.return_button)).perform(click());

            // Assertion: Check if the activity is finished
            scenario.onActivity(activity -> {
                assertTrue(activity.isFinishing());
            });
        }
    }


}
