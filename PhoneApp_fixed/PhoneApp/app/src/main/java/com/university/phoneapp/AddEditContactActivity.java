package com.university.phoneapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for both adding a new contact and editing an existing one.
 *
 * Extras:
 *   EXTRA_CONTACT (Contact, Serializable) — pass to edit; omit to add
 *
 * Result:
 *   RESULT_OK when contact was saved successfully
 */
public class AddEditContactActivity extends AppCompatActivity {

    public static final String EXTRA_CONTACT  = "contact";
    public static final int    REQUEST_ADD    = 200;
    public static final int    REQUEST_EDIT   = 201;

    private DatabaseHelper db;
    private Contact        editingContact; // null when adding

    private EditText etName, etPhone, etEmail;
    private TextView tvAvatarInitial, tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        db             = DatabaseHelper.getInstance(this);
        etName         = findViewById(R.id.etName);
        etPhone        = findViewById(R.id.etPhone);
        etEmail        = findViewById(R.id.etEmail);
        tvAvatarInitial = findViewById(R.id.tvAvatarInitial);
        tvTitle        = findViewById(R.id.tvTitle);

        // Check if editing
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_CONTACT)) {
            editingContact = (Contact) intent.getSerializableExtra(EXTRA_CONTACT);
            populateFields();
            tvTitle.setText(R.string.edit_contact);
        }

        // Live avatar initial from name field
        etName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                String t = s.toString().trim();
                tvAvatarInitial.setText(t.isEmpty() ? "?" :
                        String.valueOf(t.charAt(0)).toUpperCase());
            }
        });

        // Back / close
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Save
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveContact());
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private void populateFields() {
        etName.setText(editingContact.getName());
        etPhone.setText(editingContact.getPhone());
        etEmail.setText(editingContact.getEmail());
        tvAvatarInitial.setText(editingContact.getInitial());
    }

    // ── Save logic ────────────────────────────────────────────────────────────

    private void saveContact() {
        String name  = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return;
        }

        if (editingContact == null) {
            // ── ADD ───────────────────────────────────────────────────────────
            Contact newContact = new Contact(0, name, phone, email);
            long id = db.insertContact(newContact);
            if (id > 0) {
                Toast.makeText(this, "Contact saved!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to save contact.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // ── EDIT ──────────────────────────────────────────────────────────
            editingContact.setName(name);
            editingContact.setPhone(phone);
            editingContact.setEmail(email);
            int rows = db.updateContact(editingContact);
            if (rows > 0) {
                Toast.makeText(this, "Contact updated!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update contact.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
