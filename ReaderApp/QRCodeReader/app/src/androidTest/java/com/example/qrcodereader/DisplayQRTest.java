package com.example.qrcodereader;
import static com.example.qrcodereader.CustomMatchers.hasDrawable;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static java.util.regex.Pattern.matches;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.GrantPermissionRule;

import com.example.qrcodereader.entity.FirestoreManager;
import com.example.qrcodereader.ui.eventPage.OrganizerEventActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

public class DisplayQRTest {
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    @Before
    public void setUp() {
        // Set the Firestore collections to test versions
        FirestoreManager.getInstance().setEventCollection("eventsTest");
        FirestoreManager.getInstance().setUserCollection("usersTest");
        FirestoreManager.getInstance().setUserDocRef("1d141a0fd4e29d60");
        FirestoreManager.getInstance().setEventDocRef("6NRHwbgGk0449AVOBPLs");
    }

    // Test the display of the QR code
    @Test
    public void testCheckInQRCode() {
        // Prepare the intent with extras
        Intent startIntent = new Intent(ApplicationProvider.getApplicationContext(), DisplayQRCode.class);
        startIntent.putExtra("qrCode", "jDOzpR6G7*+w#QjVoM78jPaJBQL2*#");
        startIntent.putExtra("promotionalQRCode", "j=QhHY-x&-&WiZeTJdz=yU)t+R*Rt*");

        // Start the activity with the prepared intent
        try (ActivityScenario<DisplayQRCode> scenario = ActivityScenario.launch(startIntent)) {
            // Check if the activity is launched
            scenario.onActivity(activity -> {
                // Check if the QR code is displayed
                ImageView qrCodeImageView = activity.findViewById(R.id.qrCodeImageView);
                assertNotNull(qrCodeImageView);
                assertTrue(qrCodeImageView.getDrawable() != null);
                assert(activity.getDisplayingQR().equals("jDOzpR6G7*+w#QjVoM78jPaJBQL2*#"));
                assert(activity.getType().equals("Check in"));
            });
        }
    }

    // Test the display of the promotional QR code
    @Test
    public void testPromotionalQRCode() {
        // Prepare the intent with extras
        Intent startIntent = new Intent(ApplicationProvider.getApplicationContext(), DisplayQRCode.class);
        startIntent.putExtra("qrCode", "jDOzpR6G7*+w#QjVoM78jPaJBQL2*#");
        startIntent.putExtra("promotionalQRCode", "j=QhHY-x&-&WiZeTJdz=yU)t+R*Rt*");

        // Start the activity with the prepared intent
        try (ActivityScenario<DisplayQRCode> scenario = ActivityScenario.launch(startIntent)) {
            // Check if the activity is launched
            scenario.onActivity(activity -> {
                TextView switchButton = activity.findViewById(R.id.switch_button);
                switchButton.performClick();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Check if the QR code is displayed
                ImageView qrCodeImageView = activity.findViewById(R.id.qrCodeImageView);
                assertNotNull(qrCodeImageView);
                assertTrue(qrCodeImageView.getDrawable() != null);
                assert(activity.getDisplayingQR().equals("j=QhHY-x&-&WiZeTJdz=yU)t+R*Rt*"));
                assert(activity.getType().equals("Promotional"));
            });
        }
    }

    // Test switching between QR codes
    @Test
    public void testSwitchQRCode() {
        // Prepare the intent with extras
        Intent startIntent = new Intent(ApplicationProvider.getApplicationContext(), DisplayQRCode.class);
        startIntent.putExtra("qrCode", "jDOzpR6G7*+w#QjVoM78jPaJBQL2*#");
        startIntent.putExtra("promotionalQRCode", "j=QhHY-x&-&WiZeTJdz=yU)t+R*Rt*");

        // Start the activity with the prepared intent
        try (ActivityScenario<DisplayQRCode> scenario = ActivityScenario.launch(startIntent)) {
            // Check if the activity is launched
            scenario.onActivity(activity -> {
                // By default it displays the check in QR code
                ImageView qrCodeImageView = activity.findViewById(R.id.qrCodeImageView);
                TextView typeTextView = activity.findViewById(R.id.QR_type);
                assertNotNull(qrCodeImageView);
                assertTrue(qrCodeImageView.getDrawable() != null);
                assert(typeTextView.getText().equals("Check in"));
                assert(activity.getDisplayingQR().equals("jDOzpR6G7*+w#QjVoM78jPaJBQL2*#"));

                TextView switchButton = activity.findViewById(R.id.switch_button);
                switchButton.performClick();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                assertNotNull(qrCodeImageView);
                assertTrue(qrCodeImageView.getDrawable() != null);
                assert(typeTextView.getText().equals("Promotional"));
                assert(activity.getDisplayingQR().equals("j=QhHY-x&-&WiZeTJdz=yU)t+R*Rt*"));

                switchButton.performClick();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Check if the QR code is displayed
                assertNotNull(qrCodeImageView);
                assertTrue(qrCodeImageView.getDrawable() != null);
                assert(typeTextView.getText().equals("Check in"));
                assert(activity.getDisplayingQR().equals("jDOzpR6G7*+w#QjVoM78jPaJBQL2*#"));
            });
        }
    }
}
