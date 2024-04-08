//package com.example.qrcodereader;
//
//import androidx.test.espresso.Espresso;
//import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.matcher.ViewMatchers;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.filters.LargeTest;
//import androidx.test.rule.ActivityTestRule;
//
//import com.example.qrcodereader.entity.FirestoreManager;
//import com.example.qrcodereader.ui.eventPage.EventDetailsAttendeeActivity;
//import com.example.qrcodereader.ui.eventPage.EventRemoveAttendeeActivity;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentSnapshot;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//@RunWith(AndroidJUnit4.class)
//@LargeTest
//public class NotifierTest {
//
//    private static final long TIMEOUT_SECONDS = 10;
//
//    @Rule
//    public ActivityTestRule<EventDetailsAttendeeActivity> mActivityRuleAttendee = new ActivityTestRule<>(EventDetailsAttendeeActivity.class);
//
//    @Rule
//    public ActivityTestRule<EventRemoveAttendeeActivity> mActivityRuleRemoveAttendee = new ActivityTestRule<>(EventRemoveAttendeeActivity.class);
//
//    @Before
//    public void setUp() {
//        // Set the Firestore collections to test versions
//        FirestoreManager.getInstance().setEventCollection("eventsTest");
//        FirestoreManager.getInstance().setUserCollection("usersTest");
//        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
//        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");
//    }
//
//    @Test
//    public void SignUpAndRemoveTest() throws InterruptedException {
//        // Sign up test
//        mActivityRuleAttendee.getActivity();
//        // Wait for the activity to launch
//        Thread.sleep(5000);
//
//        // Perform signup action
//        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click());
//        Thread.sleep(3000); // Wait for signup operation to complete
//
//        // Verify changes in Firestore
//        verifySignUp();
//
//        // Remove attendee test
//        mActivityRuleRemoveAttendee.getActivity();
//        // Wait for the activity to launch
//        Thread.sleep(5000);
//
//        // Perform remove attendee action
//        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button)).perform(ViewActions.click());
//        Thread.sleep(3000); // Wait for remove attendee operation to complete
//
//        // Verify changes in Firestore
//        verifyRemoveAttendee();
//    }
//
//    private void verifySignUp() throws InterruptedException {
//        final CountDownLatch latch = new CountDownLatch(2);
//
//        // Verify event attendee document
//        FirestoreManager.getInstance().getEventDocRef().get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    Map<String, Object> map = (Map<String, Object>) document.getData().get("attendees");
//                    assertNotNull(map);
//                    assertTrue(map.containsKey("1d141a0fd4e29d60"));
//                    latch.countDown();
//                }
//            }
//        });
//
//        // Verify user eventsAttended document
//        FirestoreManager.getInstance().getUserDocRef().get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    Map<String, Object> map = (Map<String, Object>) document.getData().get("eventsAttended");
//                    assertNotNull(map);
//                    assertTrue(map.containsKey("6NRHwbgGk0449AVOBPLs"));
//                    latch.countDown();
//                }
//            }
//        });
//
//        latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
//    }
//
//    private void verifyRemoveAttendee() throws InterruptedException {
//        final CountDownLatch latch = new CountDownLatch(2);
//
//        // Verify event attendee document after removal
//        FirestoreManager.getInstance().getEventDocRef().get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    Map<String, Object> map = (Map<String, Object>) document.getData().get("attendees");
//                    assertNotNull(map);
//                    assertFalse(map.containsKey("1d141a0fd4e29d60"));
//                    latch.countDown();
//                }
//            }
//        });
//
//        // Verify user eventsAttended document after removal
//        FirestoreManager.getInstance().getUserDocRef().get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    Map<String, Object> map = (Map<String, Object>) document.getData().get("eventsAttended");
//                    assertNotNull(map);
//                    assertFalse(map.containsKey("6NRHwbgGk0449AVOBPLs"));
//                    latch.countDown();
//                }
//            }
//        });
//
//        latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
//    }
//}
