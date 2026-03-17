package com.university.phoneapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Fragment that hosts the numeric dial pad.
 *
 * Features:
 *  - 12-key dial pad (0-9, *, #)
 *  - Backspace (short press removes one char; long press clears all)
 *  - Call button that delegates to CallHelper
 */
public class DialerFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_CALL = 101;

    private TextView tvDisplay;
    private StringBuilder dialedNumber = new StringBuilder();

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dialer, container, false);

        tvDisplay = root.findViewById(R.id.tvDisplay);

        // Wire up all 12 dial keys
        int[] keyIds = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9, R.id.btnStar, R.id.btnHash
        };
        for (int id : keyIds) {
            Button btn = root.findViewById(id);
            btn.setOnClickListener(this);
        }

        // Backspace
        ImageView btnBackspace = root.findViewById(R.id.btnBackspace);
        btnBackspace.setOnClickListener(v -> deleteLastChar());
        btnBackspace.setOnLongClickListener(v -> {
            dialedNumber.setLength(0);
            updateDisplay();
            return true;
        });

        // Call button
        ImageView btnCall = root.findViewById(R.id.btnCall);
        btnCall.setOnClickListener(v -> initiateCall());

        return root;
    }

    // ── View.OnClickListener ──────────────────────────────────────────────────

    @Override
    public void onClick(View v) {
        // Each dial key stores its digit/char in the android:tag attribute
        Object tag = v.getTag();
        if (tag != null) {
            dialedNumber.append(tag.toString());
            updateDisplay();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void deleteLastChar() {
        if (dialedNumber.length() > 0) {
            dialedNumber.deleteCharAt(dialedNumber.length() - 1);
            updateDisplay();
        }
    }

    private void updateDisplay() {
        String number = dialedNumber.toString();
        tvDisplay.setText(formatDisplay(number));
    }

    /**
     * Lightweight formatter: groups a 10-digit sequence as (XXX) XXX-XXXX.
     * All other lengths are shown as-is.
     */
    private String formatDisplay(String raw) {
        if (raw.length() == 10 && raw.matches("\\d+")) {
            return "(" + raw.substring(0, 3) + ") " +
                    raw.substring(3, 6) + "-" +
                    raw.substring(6);
        }
        return raw;
    }

    private void initiateCall() {
        String number = dialedNumber.toString();
        CallHelper.call(getActivity(), number, REQUEST_CALL);
    }

    /** Called by MainActivity when it receives the permission result. */
    public void onPermissionResult(int requestCode, int[] grantResults) {
        CallHelper.onRequestPermissionsResult(
                getActivity(),
                requestCode,
                grantResults,
                dialedNumber.toString(),
                REQUEST_CALL
        );
    }

    /** Let the phonebook pre-populate the dialer display. */
    public void setNumber(String number) {
        dialedNumber = new StringBuilder(number);
        if (tvDisplay != null) updateDisplay();
    }
}
