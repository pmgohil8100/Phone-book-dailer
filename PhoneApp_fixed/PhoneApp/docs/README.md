# UniPhone — Android Phone Dialer & Phonebook
### University Android Project | Java + XML

---

## Project Overview

UniPhone is a fully functional Android application built with **Java** and **XML layouts**
(no third-party UI frameworks). It demonstrates core Android development concepts taught
in university-level mobile-app courses.

---

## Features

| Feature | Description |
|---|---|
| **Dial Pad** | 12-key numeric keypad with backspace (long-press to clear) |
| **Number Display** | Auto-formats 10-digit US numbers as `(XXX) XXX-XXXX` |
| **Make a Call** | Tapping the green call button dials the number via `ACTION_CALL` |
| **Contact List** | Alphabetically sorted list with avatar initials |
| **Live Search** | Filter contacts by name in real time |
| **Add Contact** | Form with name, phone, optional email; live avatar preview |
| **Edit Contact** | Pre-filled form; updates SQLite row |
| **Delete Contact** | Confirmation dialog before deletion |
| **Quick Call** | Call icon on each contact row — no need to open detail screen |
| **Runtime Permissions** | Requests `CALL_PHONE` at runtime (Android 6+) |
| **Persistent Storage** | SQLite database via `SQLiteOpenHelper` |
| **Sample Data** | 5 seed contacts inserted on first launch |

---

## Project Structure

```
PhoneApp/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/university/phoneapp/
│       │   ├── Contact.java                ← Data model (Serializable)
│       │   ├── DatabaseHelper.java         ← SQLite CRUD (Singleton)
│       │   ├── ContactAdapter.java         ← Custom ArrayAdapter (ViewHolder)
│       │   ├── CallHelper.java             ← Permission + Intent utility
│       │   ├── MainActivity.java           ← Fragment host + bottom nav
│       │   ├── DialerFragment.java         ← Dial pad Fragment
│       │   ├── PhonebookFragment.java      ← Contact list Fragment
│       │   ├── AddEditContactActivity.java ← Add / Edit form
│       │   └── ContactDetailActivity.java  ← Contact detail + actions
│       └── res/
│           ├── layout/
│           │   ├── activity_main.xml
│           │   ├── fragment_dialer.xml
│           │   ├── fragment_phonebook.xml
│           │   ├── item_contact.xml
│           │   ├── activity_add_edit_contact.xml
│           │   └── activity_contact_detail.xml
│           ├── drawable/
│           │   ├── dialer_key_bg.xml   ← Ripple on oval
│           │   ├── bg_call_btn.xml     ← Green call button
│           │   ├── bg_card.xml         ← Rounded white card
│           │   ├── bg_avatar.xml       ← Circle avatar
│           │   ├── bg_fab.xml          ← FAB ripple
│           │   ├── bg_bottom_nav.xml   ← Rounded top corners
│           │   └── bg_input.xml        ← Soft rounded input field
│           └── values/
│               ├── strings.xml
│               ├── colors.xml
│               └── styles.xml
├── build.gradle
└── settings.gradle
```

---

## Android Concepts Demonstrated

1. **Fragments** — `DialerFragment`, `PhonebookFragment` hosted in one Activity
2. **SQLiteOpenHelper** — Full CRUD with parameterised queries
3. **Custom ArrayAdapter** — ViewHolder pattern for performant ListView
4. **Runtime Permissions** — `CALL_PHONE` requested at runtime
5. **Intents** — `ACTION_CALL`, `startActivityForResult`, `putExtra` / `getSerializableExtra`
6. **Custom Drawables** — Ripple, Shape, with colour selectors
7. **Styles & Themes** — `styles.xml` with a custom `DialerKey` style
8. **Fragment Transactions** — Show / hide without re-creating fragments
9. **Serializable** — Passing `Contact` objects between Activities
10. **AlertDialog** — Confirmation before destructive action

---

## How to Open in Android Studio

1. Open **Android Studio** → *Open* → select the `PhoneApp/` folder
2. Wait for Gradle sync to complete
3. Connect an Android device or start an emulator (API 21+)
4. Click **▶ Run**

---

## Permissions Required

```xml
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.READ_CONTACTS" />
<uses-permission android:name="android.permission.WRITE_CONTACTS" />
<uses-permission android:name="android.permission.READ_CALL_LOG" />
```

`CALL_PHONE` is requested at **runtime** on Android 6.0+.

---

## Minimum SDK

- **minSdk 21** (Android 5.0 Lollipop) — covers ~99% of active devices
- **targetSdk 34** (Android 14)

---

## Colour Palette

| Token | Hex | Usage |
|---|---|---|
| `primary` | `#1A237E` | App bar, FAB, labels |
| `accent` | `#00E5FF` | Accent highlights |
| `call_green` | `#00C853` | Call button |
| `delete_red` | `#FF1744` | Delete actions |
| `background` | `#F5F6FA` | Screen background |
| `surface` | `#FFFFFF` | Cards |

---

*Developed as a university Android programming assignment.*
