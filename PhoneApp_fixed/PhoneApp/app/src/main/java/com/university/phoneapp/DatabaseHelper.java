package com.university.phoneapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLiteOpenHelper that manages the local contacts database.
 *
 * Table: contacts
 *   _id   INTEGER PRIMARY KEY AUTOINCREMENT
 *   name  TEXT NOT NULL
 *   phone TEXT NOT NULL
 *   email TEXT
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // ── Constants ─────────────────────────────────────────────────────────────
    private static final String DB_NAME    = "phoneapp.db";
    private static final int    DB_VERSION = 1;

    public  static final String TABLE      = "contacts";
    public  static final String COL_ID     = "_id";
    public  static final String COL_NAME   = "name";
    public  static final String COL_PHONE  = "phone";
    public  static final String COL_EMAIL  = "email";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE + " (" +
            COL_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAME  + " TEXT NOT NULL, " +
            COL_PHONE + " TEXT NOT NULL, " +
            COL_EMAIL + " TEXT" +
            ");";

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ── SQLiteOpenHelper ──────────────────────────────────────────────────────

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        seedSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // ── CRUD Operations ───────────────────────────────────────────────────────

    /** Insert a new contact. Returns the new row id, or -1 on failure. */
    public long insertContact(Contact c) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = toContentValues(c);
        long id = db.insert(TABLE, null, cv);
        db.close();
        return id;
    }

    /** Return all contacts ordered alphabetically by name. */
    public List<Contact> getAllContacts() {
        List<Contact> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE,
                null,               // all columns
                null, null,         // no WHERE
                null, null,
                COL_NAME + " ASC"   // ORDER BY name
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToContact(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /** Search contacts whose name contains the query string (case-insensitive). */
    public List<Contact> searchContacts(String query) {
        List<Contact> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE,
                null,
                COL_NAME + " LIKE ?",
                new String[]{"%" + query + "%"},
                null, null,
                COL_NAME + " ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToContact(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /** Fetch a single contact by id. Returns null if not found. */
    public Contact getContactById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE,
                null,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Contact contact = null;
        if (cursor.moveToFirst()) {
            contact = cursorToContact(cursor);
        }
        cursor.close();
        db.close();
        return contact;
    }

    /** Update an existing contact. Returns number of rows affected. */
    public int updateContact(Contact c) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.update(
                TABLE,
                toContentValues(c),
                COL_ID + " = ?",
                new String[]{String.valueOf(c.getId())}
        );
        db.close();
        return rows;
    }

    /** Delete a contact by id. Returns number of rows deleted. */
    public int deleteContact(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(
                TABLE,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ContentValues toContentValues(Contact c) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME,  c.getName());
        cv.put(COL_PHONE, c.getPhone());
        cv.put(COL_EMAIL, c.getEmail());
        return cv;
    }

    private Contact cursorToContact(Cursor cursor) {
        return new Contact(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL))
        );
    }

    /** Pre-populate with a few sample contacts so the app looks populated on first run. */
    private void seedSampleData(SQLiteDatabase db) {
        String[][] samples = {
            {"Alice Johnson",  "+1 555-0101", "alice@example.com"},
            {"Bob Smith",      "+1 555-0102", "bob@example.com"},
            {"Carol Williams", "+1 555-0103", ""},
            {"David Brown",    "+1 555-0104", "david@example.com"},
            {"Eva Martinez",   "+1 555-0105", ""},
        };
        for (String[] s : samples) {
            ContentValues cv = new ContentValues();
            cv.put(COL_NAME,  s[0]);
            cv.put(COL_PHONE, s[1]);
            cv.put(COL_EMAIL, s[2]);
            db.insert(TABLE, null, cv);
        }
    }
}
