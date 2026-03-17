package com.university.phoneapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Custom ArrayAdapter that binds a List<Contact> to the item_contact.xml layout.
 * Uses the ViewHolder pattern for efficient ListView recycling.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    // Palette of avatar background colours — cycled by list position
    private static final int[] AVATAR_COLORS = {
        Color.parseColor("#3F51B5"), // Indigo
        Color.parseColor("#E91E63"), // Pink
        Color.parseColor("#009688"), // Teal
        Color.parseColor("#FF5722"), // Deep Orange
        Color.parseColor("#673AB7"), // Deep Purple
        Color.parseColor("#2196F3"), // Blue
        Color.parseColor("#4CAF50"), // Green
        Color.parseColor("#FF9800"), // Orange
    };

    // Callback interface so the host Fragment/Activity can react to quick-call taps
    public interface OnCallClickListener {
        void onCallClick(Contact contact);
    }

    private final LayoutInflater inflater;
    private OnCallClickListener  callListener;

    public ContactAdapter(Context context, List<Contact> contacts) {
        super(context, 0, contacts);
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnCallClickListener(OnCallClickListener listener) {
        this.callListener = listener;
    }

    // ── ViewHolder pattern ────────────────────────────────────────────────────

    private static class ViewHolder {
        TextView  tvInitial;
        TextView  tvName;
        TextView  tvPhone;
        ImageView btnQuickCall;
    }

    // ── getView ───────────────────────────────────────────────────────────────

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_contact, parent, false);
            holder = new ViewHolder();
            holder.tvInitial    = convertView.findViewById(R.id.tvInitial);
            holder.tvName       = convertView.findViewById(R.id.tvName);
            holder.tvPhone      = convertView.findViewById(R.id.tvPhone);
            holder.btnQuickCall = convertView.findViewById(R.id.btnQuickCall);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Contact contact = getItem(position);
        if (contact == null) return convertView;

        // Avatar initial & colour
        holder.tvInitial.setText(contact.getInitial());
        holder.tvInitial.getBackground().setTint(
                AVATAR_COLORS[position % AVATAR_COLORS.length]);

        // Text fields
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhone());

        // Quick-call button
        holder.btnQuickCall.setOnClickListener(v -> {
            if (callListener != null) callListener.onCallClick(contact);
        });

        return convertView;
    }
}
