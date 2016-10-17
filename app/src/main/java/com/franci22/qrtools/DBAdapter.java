package com.franci22.qrtools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
    SQLiteDatabase database_ob;
    DBHelper openHelper_ob;
    Context context;

    public DBAdapter(Context c) {
        context = c;
    }

    public DBAdapter opnToRead() {
        openHelper_ob = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.VERSION);
        database_ob = openHelper_ob.getReadableDatabase();
        return this;

    }

    public DBAdapter opnToWrite() {
        openHelper_ob = new DBHelper(context,
                DBHelper.DATABASE_NAME, null, DBHelper.VERSION);
        database_ob = openHelper_ob.getWritableDatabase();
        return this;

    }

    public void Close() {
        database_ob.close();
    }

    public long insertDetails(String text, String date, String format) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.text, text);
        contentValues.put(DBHelper.date, date);
        contentValues.put(DBHelper.format, format);
        opnToWrite();
        long val = database_ob.insert(DBHelper.TABLE_NAME, null,
                contentValues);
        Close();
        return val;

    }

    public Cursor queryName() {
        String[] cols = {DBHelper.KEY_ID, DBHelper.text,
                DBHelper.date, DBHelper.format};
        opnToWrite();
        Cursor c = database_ob.query(DBHelper.TABLE_NAME, cols, null,
                null, null, null, null);

        return c;

    }

    public Cursor queryAll(int nameId) {
        String[] cols = {DBHelper.KEY_ID, DBHelper.text,
                DBHelper.date, DBHelper.format};
        opnToWrite();
        Cursor c = database_ob.query(DBHelper.TABLE_NAME, cols,
                DBHelper.KEY_ID + "=" + nameId, null, null, null, null);

        return c;

    }

    public long updateldetail(int rowId, String text, String date, String format) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.text, text);
        contentValues.put(DBHelper.date, date);
        contentValues.put(DBHelper.format, format);
        opnToWrite();
        long val = database_ob.update(DBHelper.TABLE_NAME, contentValues,
                DBHelper.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }

    public int deletOneRecord(int rowId) {
        opnToWrite();
        int val = database_ob.delete(DBHelper.TABLE_NAME,
                DBHelper.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }
    public void deleteTable(){
        opnToWrite();
        database_ob.delete(DBHelper.TABLE_NAME, null, null);
    }

}