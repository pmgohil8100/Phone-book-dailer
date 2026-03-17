package com.university.phoneapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Shows the full details of a single Contact.
 * Allows the user to call, edit, or delete the contact.
 */
public class ContactDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT = "contact";
    private static final int   REQUEST_CALL  = 103;

    private DatabaseHelper db;
    private Contact        contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        db      = DatabaseHelper.getInstance(this);
        contact = (Contact) getIntent().getSerializableExtra(EXTRA_CONTACT);

        if (contact == null) {
            finish();
            return;
        }

        bindViews();
        setupListeners();
    }

    // ── Bind data to views ────────────────────────────────────────────────────

    private void bindViews() {
        TextView tvInitial = findViewById(R.id.tvInitial);
        TextView tvName    = findViewById(R.id.tvName);
        TextView tvPhone   = findViewById(R.id.tvPhone);
        TextView tvEmail   = findViewById(R.id.tvEmail);
        LinearLayout rowEmail = findViewById(R.id.rowEmail);

        tvInitial.setText(contact.getInitial());
        tvName.setText(contact.getName());
        tvPhone.setText(contact.getPhone());

        String email = contact.getEmail();
        if (email != null && !email.isEmpty()) {
            tvEmail.setText(email);
            rowEmail.setVisibility(View.VISIBLE);
        } else {
            rowEmail.setVisibility(View.GONE);
        }
    }

    // ── Button listeners ──────────────────────────────────────────────────────

    private void setupListeners() {
        // Back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Edit
        ImageView btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, AddEditContactActivity.class);
            i.putExtra(AddEditContactActivity.EXTRA_CONTACT, contact);
            startActivityForResult(i, AddEditContactActivity.REQUEST_EDIT);
        });

        // Delete
        ImageView btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> confirmDelete());

        // Call button
        LinearLayout btnCallDetail = findViewById(R.id.btnCallDetail);
        btnCallDetail.setOnClickListener(v ->
                CallHelper.call(this, contact.getPhone(), REQUEST_CALL));
    }

    // ── Delete with confirmation dialog ───────────────────────────────────────

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Contact")
                .setMessage("Delete " + contact.getName() + "?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    int rows = db.deleteContact(contact.getId());
                    if (rows > 0) {
                        Toast.makeText(this, "Contact deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    // ── Activity results ──────────────────────────────────────────────────────

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Reload the contact from DB after editing
            Contact updated = db.getContactById(contact.getId());
            if (updated != null) {
                contact = updated;
                bindViews();
            }
        }
    }

    // ── Permission result ─────────────────────────────────────────────────────

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CallHelper.onRequestPermissionsResult(
                this, requestCode, grantResults,
                contact.getPhone(), REQUEST_CALL);
    }
}
