package com.example.qrcodereader;

import static org.junit.Assert.assertNotNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.qrcodereader.entity.QRCode;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for QRCode entity.
 */
@RunWith(AndroidJUnit4.class)
public class QRCodeTest {

    /**
     * Tests the creation of a QRCode object.
     */
    @Test
    public void testQRCodeCreation() {
        // Create a new QRCode object
        QRCode qrCode = new QRCode();

        // Check that the bitmap is not null
        assertNotNull(qrCode.getBitmap());
    }
}

