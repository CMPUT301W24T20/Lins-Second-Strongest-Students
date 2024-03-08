package com.example.qrcodereader.entity;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.Random;

/**
 * This class is used to generate a QR code
 * It generates a random string of letters and numbers
 * and then uses the string to generate bitmap of the QR code
 * @author Duy
 */
public class QRCode {
    Bitmap bitmap;
    String qrCodeString;

    /**
     * Constructor for the QRCode class
     * It generates a random string of letters and numbers
     * and then uses the string to generate bitmap of the QR code
     */
    public QRCode() {
        qrCodeString = generateRandomString(30);
        bitmap = generateQRCodeImage(qrCodeString);
    }

    /**
     * Constructor for the QRCode class
     * It generates a bitmap of the QR code using the given string
     * @param qrCodeString the string to be encoded in the QR code
     */
    public QRCode(String qrCodeString) {
        this.qrCodeString = qrCodeString;
        bitmap = generateQRCodeImage(this.qrCodeString);
    }

    /**
     * Getter method for the bitmap of the QR code
     * @return bitmap of the QR code
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * This method generates a random string of letters and numbers
     * @param length the length of the random string
     * @return the random string
     */
    private static String generateRandomString(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()_+-=1234567890";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        int index;
        char randomChar;

        for (int i = 0; i < length; i++) {
            index = random.nextInt(alphabet.length());
            randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    /**
     * This method generates a bitmap of the QR code
     * @param text the string to be encoded in the QR code
     * @return the bitmap of the QR code
     */
    private Bitmap generateQRCodeImage(String text) {
        int qrCodeHeight = 500;
        int qrCodeWidth = 500;

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, qrCodeWidth, qrCodeHeight);
            Bitmap bitmap = Bitmap.createBitmap(qrCodeWidth, qrCodeHeight, Bitmap.Config.RGB_565);

            for (int x = 0; x < qrCodeHeight; x++) {
                for (int y = 0; y < qrCodeWidth; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Getter method for the string of the QR code
     * @return the string represent the QR code
     */
    public String getString() {
        return qrCodeString;
    }
}
