package com.example.qrcodereader;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

import com.example.qrcodereader.entity.QRCode;

@RunWith(AndroidJUnit4.class)
public class QRCodeTest {

    @Test
    public void testQRCodeCreation() {
        // Create a new QRCode object
        QRCode qrCode = new QRCode();

        // Check that the bitmap is not null
        assertNotNull(qrCode.getBitmap());
    }
}

