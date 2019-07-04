package com.example.android.contactsapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LIST_LOADER = 0;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    ContactCursorAdapter mContactadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_menu);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                startActivity(intent);
            }
        });

        ListView mListView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        mListView.setEmptyView(emptyView);
        mContactadapter = new ContactCursorAdapter(MainActivity.this, null);
        mListView.setAdapter(mContactadapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(LOG_TAG, "clicked " + position);
                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                Uri currentPetUri = ContentUris.withAppendedId(ListContract.ContactEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(LIST_LOADER, null, this);
    }

    private void insertPerson() {

        Drawable myDrawable = getResources().getDrawable(R.drawable.dummy_pic);
        Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myLogo.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
//        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        ContentValues values = new ContentValues();
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_NAME, "Mark");
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_NUMBER, "991234467");
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_IMAGE, b);
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_EMAIL, "abc@gmail.com");
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_WORK, "123456789");


        Uri newUri = getContentResolver().insert(ListContract.ContactEntry.CONTENT_URI, values);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(ListContract.ContactEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from shop database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

//            case R.id.insert_dummy_data:
//                insertPerson();
//                return true;

            case R.id.action_delete:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ListContract.ContactEntry._ID,
                ListContract.ContactEntry.COLUMN_CONTACT_NAME,
                ListContract.ContactEntry.COLUMN_CONTACT_NUMBER,
                ListContract.ContactEntry.COLUMN_CONTACT_IMAGE,
                ListContract.ContactEntry.COLUMN_CONTACT_EMAIL,
                ListContract.ContactEntry.COLUMN_CONTACT_WORK};


        return new CursorLoader(this,
                ListContract.ContactEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mContactadapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContactadapter.swapCursor(null);
    }
}


