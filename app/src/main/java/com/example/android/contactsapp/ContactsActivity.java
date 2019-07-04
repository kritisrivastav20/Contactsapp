package com.example.android.contactsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ContactsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentPersonUri;
    EditText mName;
    EditText mNumber;
    EditText mEmail;
    EditText mWork;
    ImageView addImage;
    ImageView mImage;
    ImageView deleteImage;
    Button save;
    private boolean mItemHasChanged = false;
    private static final int LIST_LOADER = 0;
    public static final int GET_FROM_GALLERY = 3;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);
        Intent intent = getIntent();
        mCurrentPersonUri = intent.getData();
        if (mCurrentPersonUri == null) {
            setTitle("Add a contact");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit contact");
            getLoaderManager().initLoader(LIST_LOADER, null, ContactsActivity.this);
        }
        mName = (EditText) findViewById(R.id.mName);
        mNumber = (EditText) findViewById(R.id.number);
        mEmail = (EditText) findViewById(R.id.email);
        mWork = (EditText) findViewById(R.id.work);
        addImage = (ImageView) findViewById(R.id.upload_image);
        deleteImage = (ImageView) findViewById(R.id.delete_image);
        save = (Button) findViewById(R.id.save);
        mImage = (ImageView) findViewById(R.id.person_image);
        mName.setOnTouchListener(mTouchListener);
        mNumber.setOnTouchListener(mTouchListener);
        mEmail.setOnTouchListener(mTouchListener);
        mWork.setOnTouchListener(mTouchListener);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveItem();
            }
        });
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImage.setImageResource(R.mipmap.ic_person_round);
            }
        });
    }

    private boolean saveItem() {
        String nameString = mName.getText().toString().trim();
        if (nameString.length() == 0) {
            Toast.makeText(this, "Add the contact name",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        String number = mNumber.getText().toString().trim();
        if (number.length() == 0) {
            Toast.makeText(this, "Number is invalid",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        String email = mEmail.getText().toString().trim();
        if (email.length() == 0) {
            email = " ";
        }
        String work = mWork.getText().toString().trim();
        if (work.length() == 0) {
            work = " ";
        }
        Bitmap bitmap = ((BitmapDrawable) mImage.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_NAME, nameString);
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_NUMBER, number);
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_IMAGE, byteArray);
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_EMAIL, email);
        values.put(ListContract.ContactEntry.COLUMN_CONTACT_WORK, work);
        if (mCurrentPersonUri == null) {
            Uri newUri = getContentResolver().insert(ListContract.ContactEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error with saving the contact",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Contact saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentPersonUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error updating the contact",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Contact updated",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                ByteArrayOutputStream baos=new  ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
//                byte [] b=baos.toByteArray();
//                String temp= Base64.encodeToString(b, Base64.DEFAULT);
                mImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentPersonUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveItem();
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(ContactsActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(ContactsActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
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
                mCurrentPersonUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ListContract.ContactEntry.COLUMN_CONTACT_NAME);
            int numberColumnIndex = cursor.getColumnIndex(ListContract.ContactEntry.COLUMN_CONTACT_NUMBER);
            int imageColumnIndex = cursor.getColumnIndex(ListContract.ContactEntry.COLUMN_CONTACT_IMAGE);
            int emailColumnIndex = cursor.getColumnIndex(ListContract.ContactEntry.COLUMN_CONTACT_EMAIL);
            int workColumnIndex = cursor.getColumnIndex(ListContract.ContactEntry.COLUMN_CONTACT_WORK);

            String name = cursor.getString(nameColumnIndex);
            String number = cursor.getString(numberColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String work = cursor.getString(workColumnIndex);
            byte[] Image = cursor.getBlob(imageColumnIndex);
            Bitmap bp = BitmapFactory.decodeByteArray(Image, 0, Image.length);

            mName.setText(name);
            mNumber.setText(number);
            mEmail.setText(email);
            mWork.setText(work);
            mImage.setImageBitmap(bp);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.setText("");
        mNumber.setText("");
        mEmail.setText("");
        mWork.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard changes");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete item");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (mCurrentPersonUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentPersonUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Failed to delete the contact",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Contact deleted",
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

}
