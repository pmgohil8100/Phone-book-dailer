package com.university.phoneapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Utility class that centralises the logic for initiating a phone call.
 *
 * Usage (inside an Activity):
 *   CallHelper.call(this, "+15550101", REQUEST_CALL_PERMISSION);
 *
 * Then in onRequestPermissionsResult():
 *   CallHelper.onRequestPermissionsResult(this, requestCode, grantResults,
 *                                         pendingNumber, REQUEST_CALL_PERMISSION);
 */
public class CallHelper {

    private CallHelper() {} // utility class — no instances

    /**
     * Initiate a call to {@code number}.
     * If the CALL_PHONE permission has not been granted, request it first.
     *
     * @param activity    Calling Activity (needed for permission request and Intent)
     * @param number      Phone number string, e.g. "+15550101"
     * @param requestCode The int code used in requestPermissions / onRequestPermissionsResult
     */
    public static void call(Activity activity, String number, int requestCode) {
        if (number == null || number.trim().isEmpty()) {
            Toast.makeText(activity, "No number to call", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            placeCall(activity, number);
        } else {
            // Store the number so we can retry after the dialog
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.CALL_PHONE},
                    requestCode
            );
        }
    }

    /**
     * Call this from your Activity's onRequestPermissionsResult to complete
     * the call if the user just granted the permission.
     */
    public static void onRequestPermissionsResult(Activity activity,
                                                   int requestCode,
                                                   int[] grantResults,
                                                   String pendingNumber,
                                                   int expectedRequestCode) {
        if (requestCode == expectedRequestCode) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                placeCall(activity, pendingNumber);
            } else {
                Toast.makeText(activity,
                        activity.getString(R.string.permission_denied),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private static void placeCall(Activity activity, String number) {
        Intent intent = new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + Uri.encode(number)));
        activity.startActivity(intent);
    }
}
