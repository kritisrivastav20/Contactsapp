package com.example.android.contactsapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ListContract {
    private ListContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.contactsapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SHOP = "phonebook";

    public static final class ContactEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHOP);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOP;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOP;

        public final static String TABLE_NAME = "Contact";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_CONTACT_NAME = "name";
        public final static String COLUMN_CONTACT_NUMBER = "number";
        public final static String COLUMN_CONTACT_IMAGE = "image";
        public final static String COLUMN_CONTACT_EMAIL = "email";
        public final static String COLUMN_CONTACT_WORK = "work";

    }
}
