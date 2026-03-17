package com.university.phoneapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Fragment showing the alphabetically sorted contact list.
 *
 * Features:
 *  - Live search filter
 *  - Tap a row → ContactDetailActivity
 *  - Quick-call icon on each row
 *  - FAB to add a new contact
 */
public class PhonebookFragment extends Fragment {

    private static final int REQUEST_CALL = 102;

    private DatabaseHelper  db;
    private ContactAdapter  adapter;
    private ListView        listContacts;
    private TextView        tvEmpty;
    private String          pendingCallNumber = "";

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_phonebook, container, false);

        db           = DatabaseHelper.getInstance(getActivity());
        listContacts = root.findViewById(R.id.listContacts);
        tvEmpty      = root.findViewById(R.id.tvEmpty);

        // Search bar
        EditText etSearch = root.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                loadContacts(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // FAB
        ImageView fabAdd = root.findViewById(R.id.fabAddContact);
        fabAdd.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), AddEditContactActivity.class);
            startActivityForResult(i, AddEditContactActivity.REQUEST_ADD);
        });

        loadContacts("");
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadContacts(""); // refresh after returning from detail/add screens
    }

    // ── Data ──────────────────────────────────────────────────────────────────

    private void loadContacts(String query) {
        List<Contact> contacts = query.isEmpty()
                ? db.getAllContacts()
                : db.searchContacts(query);

        if (adapter == null) {
            adapter = new ContactAdapter(getActivity(), contacts);
            adapter.setOnCallClickListener(contact -> {
                pendingCallNumber = contact.getPhone();
                CallHelper.call(getActivity(), pendingCallNumber, REQUEST_CALL);
            });
            listContacts.setAdapter(adapter);

            // Row tap → detail screen
            listContacts.setOnItemClickListener((parent, view, pos, id) -> {
                Contact c = adapter.getItem(pos);
                if (c == null) return;
                Intent i = new Intent(getActivity(), ContactDetailActivity.class);
                i.putExtra(ContactDetailActivity.EXTRA_CONTACT, c);
                startActivity(i);
            });
        } else {
            adapter.clear();
            adapter.addAll(contacts);
            adapter.notifyDataSetChanged();
        }

        tvEmpty.setVisibility(contacts.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // ── Permission result forwarded from MainActivity ─────────────────────────

    public void onPermissionResult(int requestCode, int[] grantResults) {
        CallHelper.onRequestPermissionsResult(
                getActivity(),
                requestCode,
                grantResults,
                pendingCallNumber,
                REQUEST_CALL
        );
    }
}
