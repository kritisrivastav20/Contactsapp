package com.example.android.contactsapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.contactsapp.ListContract.ContactEntry;

public class ContactDBHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "contacts.db";
    public final static int DB_VERSION = 1;
    public final static String LOG_TAG = ContactDBHelper.class.getCanonicalName();

    public ContactDBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_CONTACTS = "CREATE TABLE " + ContactEntry.TABLE_NAME + "("
                + ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ContactEntry.COLUMN_CONTACT_NAME + " TEXT, "
                + ContactEntry.COLUMN_CONTACT_NUMBER + " TEXT, "
                + ContactEntry.COLUMN_CONTACT_IMAGE + " BLOB, "
                + ContactEntry.COLUMN_CONTACT_EMAIL + " TEXT, "
                + ContactEntry.COLUMN_CONTACT_WORK + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
