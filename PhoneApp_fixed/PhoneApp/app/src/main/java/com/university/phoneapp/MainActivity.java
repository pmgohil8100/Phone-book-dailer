package com.university.phoneapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Single-Activity host that swaps between DialerFragment and PhonebookFragment
 * using the custom bottom navigation bar defined in activity_main.xml.
 */
public class MainActivity extends AppCompatActivity {

    // Fragment tags
    private static final String TAG_DIALER    = "dialer";
    private static final String TAG_PHONEBOOK = "phonebook";

    private DialerFragment    dialerFragment;
    private PhonebookFragment phonebookFragment;

    private LinearLayout tabDialer;
    private LinearLayout tabPhonebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabDialer    = findViewById(R.id.tabDialer);
        tabPhonebook = findViewById(R.id.tabPhonebook);

        // Navigation clicks
        tabDialer.setOnClickListener(v    -> showTab(0));
        tabPhonebook.setOnClickListener(v -> showTab(1));

        // Show dialer on launch
        if (savedInstanceState == null) {
            showTab(0);
        }
    }

    // ── Tab switching ─────────────────────────────────────────────────────────

    private void showTab(int index) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Lazy-create fragments
        if (dialerFragment == null) {
            dialerFragment = new DialerFragment();
        }
        if (phonebookFragment == null) {
            phonebookFragment = new PhonebookFragment();
        }

        // Hide both, then show the selected one
        if (fm.findFragmentByTag(TAG_DIALER) != null) {
            ft.hide(dialerFragment);
        }
        if (fm.findFragmentByTag(TAG_PHONEBOOK) != null) {
            ft.hide(phonebookFragment);
        }

        if (index == 0) {
            // Dialer tab
            if (fm.findFragmentByTag(TAG_DIALER) == null) {
                ft.add(R.id.contentFrame, dialerFragment, TAG_DIALER);
            } else {
                ft.show(dialerFragment);
            }
            highlightTab(true);
        } else {
            // Phonebook tab
            if (fm.findFragmentByTag(TAG_PHONEBOOK) == null) {
                ft.add(R.id.contentFrame, phonebookFragment, TAG_PHONEBOOK);
            } else {
                ft.show(phonebookFragment);
            }
            highlightTab(false);
        }

        ft.commit();
    }

    private void highlightTab(boolean dialerSelected) {
        int activeColor   = getResources().getColor(R.color.primary,   getTheme());
        int inactiveColor = getResources().getColor(R.color.text_secondary, getTheme());

        // Tint icons and labels for dialer tab
        ((android.widget.ImageView) tabDialer.getChildAt(0))
                .setColorFilter(dialerSelected ? activeColor : inactiveColor);
        ((android.widget.TextView) tabDialer.getChildAt(1))
                .setTextColor(dialerSelected ? activeColor : inactiveColor);

        // Tint icons and labels for phonebook tab
        ((android.widget.ImageView) tabPhonebook.getChildAt(0))
                .setColorFilter(!dialerSelected ? activeColor : inactiveColor);
        ((android.widget.TextView) tabPhonebook.getChildAt(1))
                .setTextColor(!dialerSelected ? activeColor : inactiveColor);
    }

    // ── Permission result ─────────────────────────────────────────────────────

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward to whichever fragment requested the permission
        if (dialerFragment != null && dialerFragment.isAdded()) {
            dialerFragment.onPermissionResult(requestCode, grantResults);
        }
        if (phonebookFragment != null && phonebookFragment.isAdded()) {
            phonebookFragment.onPermissionResult(requestCode, grantResults);
        }
    }
}
