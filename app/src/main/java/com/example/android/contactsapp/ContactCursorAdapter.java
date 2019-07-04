package com.example.android.contactsapp;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactCursorAdapter extends CursorAdapter {

    public ContactCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int id = cursor.getInt(cursor.getColumnIndex(ListContract.ContactEntry._ID));

        TextView productName = (TextView) view.findViewById(R.id.final_name);
        ImageView image = (ImageView) view.findViewById(R.id.final_image);
        ImageView call = (ImageView) view.findViewById(R.id.call);
        int nameColumnIndex = cursor.getColumnIndex(ListContract.ContactEntry.COLUMN_CONTACT_NAME);
        int imageColumnIndex = cursor.getColumnIndex(ListContract.ContactEntry.COLUMN_CONTACT_IMAGE);
        int numberColumnIndex = cursor.getColumnIndex(ListContract.ContactEntry.COLUMN_CONTACT_NUMBER);

        byte[] Image = cursor.getBlob(imageColumnIndex);
        Bitmap bp = BitmapFactory.decodeByteArray(Image, 0, Image.length);
        String name = cursor.getString(nameColumnIndex);
        final String number = cursor.getString(numberColumnIndex);

        productName.setText(name);
        image.setImageBitmap(bp);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
                view.getContext().startActivity(intent);
            }
        });
    }

}



