package com.university.phoneapp;

import java.io.Serializable;

/**
 * Model class representing a single Contact entry.
 * Implements Serializable so it can be passed between Activities via Intent extras.
 */
public class Contact implements Serializable {

    private int id;          // unique row id in SQLite
    private String name;
    private String phone;
    private String email;    // optional

    // ── Constructors ──────────────────────────────────────────────────────────

    public Contact() {}

    public Contact(int id, String name, String phone, String email) {
        this.id    = id;
        this.name  = name;
        this.phone = phone;
        this.email = email;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public int    getId()    { return id; }
    public void   setId(int id) { this.id = id; }

    public String getName()  { return name; }
    public void   setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void   setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void   setEmail(String email) { this.email = email; }

    /** Returns first letter of name, upper-cased, for the avatar circle. */
    public String getInitial() {
        if (name != null && !name.isEmpty()) {
            return String.valueOf(name.charAt(0)).toUpperCase();
        }
        return "?";
    }
}
