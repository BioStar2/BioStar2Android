package com.supremainc.biostar2.util;


import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utils {
    private final static String TAG = "Utils";

    public static int convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void toHexString(String index, String data) {
        byte[] buf = HexStringToByteArray(data);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            sb.append(Integer.toHexString(0x0100 + (buf[i] & 0x00FF)).substring(1));
        }
    }

    public static void toHexString(String index, byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            sb.append(Integer.toHexString(0x0100 + (buf[i] & 0x00FF)).substring(1));
        }
    }

    public static void Log(String tt) {
//        AppDataProvider app = AppDataProvider.getInstance(null);
//        StringBuilder sb = new StringBuilder();
//        StringBuilder sb2 = new StringBuilder();
//        for (int i=0; i < tt.length(); i++) {
//            int test = tt.charAt(i);
//            sb.append(" 0x"+Integer.toHexString( test ));
//        }
//        for (int i=0; i < tt.length(); i++) {
//            int test = tt.charAt(i);
//            sb2.append(" "+test);
//        }
//        app.test(tt+"\n"+sb.toString()+"\n"+sb2.toString());
//        Log.e(TAG,sb.toString());
    }

    public static String toHexString(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            sb.append(Integer.toHexString(0x0100 + (buf[i] & 0x00FF)).substring(1));
        }
        return sb.toString();
    }

    public static String ByteArraytoHexString(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex
        // characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned
            // value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from
            // upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character
            // from lower nibble
        }
        return new String(hexChars);
    }
}
