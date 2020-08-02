package com.example.crush;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    Context context;
    private static final String DBname = "users";
    private static final String TB_NAME = "followers";


    private static final String CMD = "CREATE TABLE " + TB_NAME + " ("
            + following.Key_ID + " long PRIMARY KEY NOT NULL, "
            + following.KEY_NAME + " TEXT, "
            + following.KEY_IMAGE + " TEXT "+
            ");";

    public DbHelper(@Nullable Context context) {
        super(context, DBname, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD);
        Toast.makeText(context, "created", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF NOT EXISTS " + TB_NAME);
        onCreate(db);

    }


    public void AddItem(following items) {

        SQLiteDatabase sd = this.getWritableDatabase();
        long insertId = sd.insert(TB_NAME, null, items.getContentValues());

        Toast.makeText(context, "added", Toast.LENGTH_SHORT).show();
        if (sd.isOpen()) sd.close();

    }

    public List<following> getItem() {

        SQLiteDatabase db = getReadableDatabase();
        List<following> lsl = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TB_NAME, null);


        if (cursor.moveToFirst()) {
            do {

                following sl = new following();

                sl.setId(cursor.getLong(cursor.getColumnIndex(following.Key_ID)));
                sl.setName(cursor.getString(cursor.getColumnIndex(following.KEY_NAME)));
                sl.setProfilePictureUrl(cursor.getString(cursor.getColumnIndex(following.KEY_IMAGE)));



                lsl.add(sl);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (db.isOpen()) db.close();
        return lsl;

    }

}