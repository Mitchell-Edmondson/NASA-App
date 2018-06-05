package com.example.android.nasaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Mitchell on 18/11/17.
 */

public class Storage extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "Photographers.db";
    private static final String TABLE_NAME = "Photographer_Table";
    private static final String COL_1 = "photographerName";

    //This creates the database
    public Storage(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        //Create the table with column name
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " (" + COL_1 + ")" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    //Insert the photographers name into the database
    public void insertdata(String s)
    {
        //Delete previous entry
        deleteEntry();

        SQLiteDatabase database = this.getWritableDatabase();
        //stores the photographer's name in the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, s);

        //Safety check
        long result = database.insert(TABLE_NAME, null, contentValues);
        Log.d("STORAGE_CHECK", "insertdata: Added " + s + " to " + TABLE_NAME +
        "\nResult = " + result);
    }

    //Return the photographers name
    public String recieveData()
    {
        SQLiteDatabase database = this.getReadableDatabase();
        String[] projection= {COL_1};
        //only want 1 thing, so everything else can be null
        Cursor cursor = database.query(TABLE_NAME, projection, null, null, null, null, null);
        //go to last row in table and return that
        cursor.moveToLast();
        //getString(0) is for the column, so column 0, last row
        return cursor.getString(0);
    }

    //Delete previous entry so that way it's not an endless table, will always have 1 row with the photographer's name
    public void deleteEntry()
    {
        SQLiteDatabase database = this.getReadableDatabase();
        int check = database.delete(TABLE_NAME, "1", null);
        Log.d("DELETE_CHECK", "DELETED DATA: " + check);

    }
}