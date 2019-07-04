package com.example.android.contactsapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.contactsapp.ListContract.ContactEntry;

public class ContactsProvider extends ContentProvider {
    public static final String LOG_TAG = ContactsProvider.class.getSimpleName();
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ListContract.CONTENT_AUTHORITY, ListContract.PATH_SHOP, ITEMS);
        sUriMatcher.addURI(ListContract.CONTENT_AUTHORITY, ListContract.PATH_SHOP + "/#", ITEM_ID);
    }

    private ContactDBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ContactDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                cursor = database.query(ListContract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ITEM_ID:
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ContactEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {

        String name = values.getAsString(ContactEntry.COLUMN_CONTACT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item requires a name");
        }
        String number = values.getAsString(ContactEntry.COLUMN_CONTACT_NUMBER);
        if (number == null) {
            throw new IllegalArgumentException("Item requires valid number");
        }
        String email = values.getAsString(ContactEntry.COLUMN_CONTACT_EMAIL);
        if (email == null) {
            throw new IllegalArgumentException("Item requires valid email");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ContactEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ContactEntry.COLUMN_CONTACT_NAME)) {
            String name = values.getAsString(ContactEntry.COLUMN_CONTACT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }
        if (values.containsKey(ContactEntry.COLUMN_CONTACT_NUMBER)) {
            String number = values.getAsString(ContactEntry.COLUMN_CONTACT_NUMBER);
            if (number == null) {
                throw new IllegalArgumentException("Item requires valid quantity");
            }
        }
        if (values.containsKey(ContactEntry.COLUMN_CONTACT_EMAIL)) {
            String email = values.getAsString(ContactEntry.COLUMN_CONTACT_EMAIL);
            if (email == null) {
                throw new IllegalArgumentException("Item requires valid price");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ContactEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete(ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ContactEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ContactEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

